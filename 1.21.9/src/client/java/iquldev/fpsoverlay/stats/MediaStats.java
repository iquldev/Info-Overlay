package iquldev.fpsoverlay.stats;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iquldev.fpsoverlay.InfoOverlay;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class MediaStats {
    private static final String WINDOWS_GSMTC_SCRIPT = """
            Add-Type -AssemblyName System.Runtime.WindowsRuntime
            $null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media, ContentType = WindowsRuntime]
            $asTask = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
                    $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1'
                })[0]
            function AwaitOp($WinRtTask, $ResultType) {
                $taskMethod = $asTask.MakeGenericMethod($ResultType)
                $netTask = $taskMethod.Invoke($null, @($WinRtTask))
                $netTask.Wait() | Out-Null
                $netTask.Result
            }
            $manager = AwaitOp ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])
            $session = $manager.GetCurrentSession()
            if ($null -eq $session) { Write-Output 'NO_SESSION'; exit 0 }
            $mediaType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]
            $media = AwaitOp ($session.TryGetMediaPropertiesAsync()) ($mediaType)
            $info = $session.GetPlaybackInfo()
            $playing = ($info.PlaybackStatus -eq [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionPlaybackStatus]::Playing)
            $tl = $session.GetTimelineProperties()
            $posMs = [long]($tl.Position.TotalMilliseconds)
            $durMs = [long]($tl.EndTime.TotalMilliseconds)
            $obj = [ordered]@{
                title      = $media.Title
                artist     = $media.Artist
                album      = $media.AlbumTitle
                progressMs = $posMs
                durationMs = $durMs
                playing    = $playing
            }
            $obj | ConvertTo-Json -Compress
            """;

    private static final String LINUX_PLAYERCTL = """
            meta=$(playerctl metadata --format '{{title}}\t{{artist}}\t{{album}}\t{{status}}\t{{mpris:length}}' 2>/dev/null) || exit 1
            printf '%s\n' "$meta"
            playerctl position 2>/dev/null
            """;

    private static volatile Path windowsScriptFile;

    private final AtomicReference<MediaInfo> info = new AtomicReference<>(new MediaInfo("", "", "", 0, 0, false));
    private final AtomicBoolean updating = new AtomicBoolean(false);
    private long lastUpdate = 0;
    private volatile long progressSampleTimeMs = System.currentTimeMillis();
    private volatile int mediaLayoutEpoch = 0;
    private static final long UPDATE_INTERVAL = 1500;
    private MediaInfo cachedInfo = null;
    private long cachedInfoTime = 0;
    private static final long CACHE_DURATION_MS = 100;

    public record MediaInfo(String artist, String title, String album, long progressMs, long durationMs, boolean isPlaying) {
        public boolean isEmpty() {
            return artist.isEmpty() && title.isEmpty();
        }
    }

    public MediaInfo getInfo() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            return new MediaInfo("", "", "", 0, 0, false);
        }

        long now = System.currentTimeMillis();
        if (cachedInfo != null && (now - cachedInfoTime) < CACHE_DURATION_MS) {
            return cachedInfo;
        }

        if (now - lastUpdate > UPDATE_INTERVAL) {
            lastUpdate = now;
            scheduleUpdate();
        }
        cachedInfo = withInterpolatedProgress(info.get(), now);
        cachedInfoTime = now;
        return cachedInfo;
    }

    public int mediaLayoutEpoch() {
        return mediaLayoutEpoch;
    }

    private void commitFetchedInfo(MediaInfo next) {
        MediaInfo prev = info.get();
        progressSampleTimeMs = System.currentTimeMillis();
        boolean layoutChanged = prev.isEmpty() != next.isEmpty()
                || !prev.title().equals(next.title())
                || !prev.artist().equals(next.artist())
                || prev.durationMs() != next.durationMs();
        info.set(next);
        if (layoutChanged) {
            mediaLayoutEpoch++;
        }
    }

    private MediaInfo withInterpolatedProgress(MediaInfo raw, long nowMs) {
        if (raw.isEmpty()) {
            return raw;
        }
        long elapsed = nowMs - progressSampleTimeMs;
        long p = raw.progressMs();
        if (raw.isPlaying()) {
            if (raw.durationMs() > 0) {
                p = Math.min(raw.durationMs(), raw.progressMs() + Math.max(0, elapsed));
            } else {
                p = raw.progressMs() + Math.max(0, elapsed);
            }
        }
        return new MediaInfo(raw.artist(), raw.title(), raw.album(), p, raw.durationMs(), raw.isPlaying());
    }

    private void scheduleUpdate() {
        if (!updating.compareAndSet(false, true)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                String os = System.getProperty("os.name", "").toLowerCase();
                if (os.contains("win")) {
                    updateWindows();
                } else if (os.contains("nux")) {
                    updateLinux();
                }
            } catch (Exception e) {
                InfoOverlay.LOGGER.error("Media widget update failed", e);
            } finally {
                updating.set(false);
            }
        });
    }

    private void updateWindows() throws IOException, InterruptedException {
        Path script = windowsScriptFile;
        if (script == null) {
            synchronized (MediaStats.class) {
                if (windowsScriptFile == null) {
                    Path p = Files.createTempFile("fpsoverlay-gsmtc", ".ps1");
                    Files.writeString(p, WINDOWS_GSMTC_SCRIPT, StandardCharsets.UTF_8);
                    p.toFile().deleteOnExit();
                    windowsScriptFile = p;
                }
                script = windowsScriptFile;
            }
        }

        ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe",
                "-NoProfile",
                "-ExecutionPolicy",
                "Bypass",
                "-File",
                script.toAbsolutePath().toString()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        if (!p.waitFor(8, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            return;
        }
        String output = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        if (output.isEmpty() || output.equals("NO_SESSION")) {
            commitFetchedInfo(new MediaInfo("", "", "", 0, 0, false));
            return;
        }
        try {
            JsonObject json = JsonParser.parseString(output).getAsJsonObject();
            String title = stringOrEmpty(json, "title");
            String artist = stringOrEmpty(json, "artist");
            String album = stringOrEmpty(json, "album");
            long progress = json.has("progressMs") && !json.get("progressMs").isJsonNull()
                    ? json.get("progressMs").getAsLong()
                    : 0L;
            long duration = json.has("durationMs") && !json.get("durationMs").isJsonNull()
                    ? json.get("durationMs").getAsLong()
                    : 0L;
            boolean playing = json.has("playing") && !json.get("playing").isJsonNull() && json.get("playing").getAsBoolean();
            commitFetchedInfo(new MediaInfo(artist, title, album, progress, duration, playing));
        } catch (Exception e) {
            InfoOverlay.LOGGER.warn("Failed to parse GSMTC JSON: {}", output);
        }
    }

    private void updateLinux() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", LINUX_PLAYERCTL.strip());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        if (!p.waitFor(6, TimeUnit.SECONDS)) {
            p.destroyForcibly();
            return;
        }
        String raw = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
        if (p.exitValue() != 0 || raw.isEmpty()) {
            commitFetchedInfo(new MediaInfo("", "", "", 0, 0, false));
            return;
        }
        String[] lines = raw.split("\n", 3);
        String metaLine = lines[0];
        double positionSec = 0;
        if (lines.length >= 2) {
            try {
                positionSec = Double.parseDouble(lines[1].trim());
            } catch (NumberFormatException ignored) {
            }
        }
        String[] parts = metaLine.split("\t", -1);
        if (parts.length < 5) {
            commitFetchedInfo(new MediaInfo("", "", "", 0, 0, false));
            return;
        }
        String title = parts[0];
        String artist = parts[1];
        String album = parts[2];
        String status = parts[3];
        long durationMicro = 0L;
        try {
            if (!parts[4].isEmpty()) {
                durationMicro = Long.parseLong(parts[4]);
            }
        } catch (NumberFormatException ignored) {
        }
        long durationMs = durationMicro / 1000L;
        long progressMs = (long) (positionSec * 1000.0);
        boolean playing = "playing".equalsIgnoreCase(status);
        commitFetchedInfo(new MediaInfo(artist, title, album, progressMs, durationMs, playing));
    }

    private static String stringOrEmpty(JsonObject json, String key) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return "";
        }
        return json.get(key).getAsString();
    }
}
