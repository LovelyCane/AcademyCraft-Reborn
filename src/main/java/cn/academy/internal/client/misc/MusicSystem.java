package cn.academy.internal.client.misc;

import cn.academy.AcademyCraft;
import cn.academy.Tags;
import cn.lambdalib2.util.ReflectionUtils;
import cn.lambdalib2.util.ResourceUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SideOnly(Side.CLIENT)
public class MusicSystem {
    public static final float DEFAULT_VOLUME = 1.0f;
    private static final String MEDIA_ID = "AC_music";
    private static final Path MUSIC_PACK_FOLDER = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks().toPath().resolve("AcademyMusicPack");
    private static final Path MUSIC_FOLDER = MUSIC_PACK_FOLDER.resolve("assets").resolve(Tags.MOD_ID).resolve("music");
    private static final Path MUSIC_FILES_FOLDER = MUSIC_FOLDER.resolve("music_files");
    private static final Path MUSIC_ICONS_FOLDER = MUSIC_FOLDER.resolve("icons");
    private static final Path MUSIC_INFO_FOLDER = MUSIC_FOLDER.resolve("info");

    private static SoundSystem soundSystem;
    public static final MusicSystem INSTANCE = new MusicSystem();

    private final List<Music> musicList = new ArrayList<>();
    // default
    private static PlayState playState = PlayState.STOPPED;

    public Music currentMusic;

    public enum PlayState {
        PLAYING, PAUSED, STOPPED
    }

    private MusicSystem() {
        createDirectoryIfNotExists(MUSIC_PACK_FOLDER, MUSIC_FOLDER, MUSIC_FILES_FOLDER, MUSIC_ICONS_FOLDER, MUSIC_INFO_FOLDER);
        loadMusicData();
    }

    public static PlayState getPlayState() {
        return playState;
    }

    private static void setPlayState(PlayState state) {
        playState = state;
    }

    // even idk why i can find this bug (null)
    @SubscribeEvent
    public static void _reInit(SoundLoadEvent evt) {
        Timer timer = new Timer();

        // because minecraft's code is after this code to run
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                initializeSoundSystem();
                setPlayState(PlayState.STOPPED);
            }
        }, 3000);
    }

    private static void initializeSoundSystem() {
        try {
            Field soundManagerField = ReflectionUtils.getObfField(SoundHandler.class, "sndManager", "field_147694_f");
            SoundManager soundManager = (SoundManager) soundManagerField.get(Minecraft.getMinecraft().getSoundHandler());

            Field soundSystemField = ReflectionUtils.getObfField(SoundManager.class, "sndSystem", "field_148620_e");
            soundSystemField.setAccessible(true);
            soundSystem = (SoundSystem) soundSystemField.get(soundManager);

            if (soundSystem == null) {
                AcademyCraft.log.error("Failed to initialize SoundSystem: soundSystem is null");
            } else {
                AcademyCraft.log.info("SoundSystem initialized successfully");
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to initialize SoundSystem", e);
        }
    }

    public List<Music> getMusicList() {
        return new ArrayList<>(musicList);
    }

    private void loadMusicData() {
        List<Path> jsonFiles = getInfoFiles();
        for (Path jsonFile : jsonFiles) {
            Music music = loadMusicFromJson(jsonFile);
            if (music != null) {
                musicList.add(music);
            }
        }
    }

    private List<Path> getInfoFiles() {
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(MUSIC_INFO_FOLDER, "*.json")) {
                List<Path> jsonFiles = new ArrayList<>();
                for (Path entry : stream) {
                    jsonFiles.add(entry);
                }
                return jsonFiles;
            }
        } catch (IOException e) {
            AcademyCraft.log.error("Error reading JSON files from directory: {}", MUSIC_INFO_FOLDER, e);
            return Collections.emptyList();
        }
    }

    private Music loadMusicFromJson(Path jsonFile) {
        try (BufferedReader reader = Files.newBufferedReader(jsonFile)) {
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);

            String name = jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : "Unknown";
            String fileName = jsonObject.get("file_name") != null ? jsonObject.get("file_name").getAsString() : "Unknown";
            String filePath = jsonObject.get("file_path") != null ? jsonObject.get("file_path").getAsString() : "default_path";
            String iconPath = jsonObject.get("icon_path") != null ? jsonObject.get("icon_path").getAsString() : "default_icon";
            String description = jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : "No description";

            return new Music(name, fileName, filePath, iconPath, description);
        } catch (IOException e) {
            AcademyCraft.log.error("Failed to load music from JSON file: {}", jsonFile, e);
            return null;
        }
    }

    public void playMusic(Music music) {
        if (music == null)
            return;

        URL musicURL = ResourceUtils.getURLForResource(new ResourceLocation(Tags.MOD_ID, music.getFilePath()));
        if (isValidFile(musicURL)) {
            soundSystem.removeSource(MEDIA_ID);
            soundSystem.newStreamingSource(true, MEDIA_ID, musicURL, music.getFileName() + ".ogg", false, 0, 0, 0, SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultAttenuation());
            soundSystem.setVolume(MEDIA_ID, DEFAULT_VOLUME);
            soundSystem.play(MEDIA_ID);
            setPlayState(PlayState.PLAYING);
        } else {
            AcademyCraft.log.error("Can't find music file from {}", music.getFilePath());
        }
    }

    public void pauseMusic() {
        if (playState == PlayState.PLAYING) {
            soundSystem.pause(MEDIA_ID);
            setPlayState(PlayState.PAUSED);
        }
    }

    public void continueMusic() {
        if (playState == PlayState.PAUSED) {
            soundSystem.play(MEDIA_ID);
            setPlayState(PlayState.PLAYING);
        }
    }

    public void stopMusic() {
        if (playState != PlayState.STOPPED) {
            soundSystem.stop(MEDIA_ID);
            setPlayState(PlayState.STOPPED);
        }
    }

    public void setVolume(float volume) {
        soundSystem.setVolume(MEDIA_ID, volume);
    }

    public float getCurrentMusicTime() {
        if (soundSystem.playing(MEDIA_ID)) {
            return soundSystem.millisecondsPlayed(MEDIA_ID) / 1000.0f;
        }
        return 0;
    }

    public float getCurrentMusicProgress() {
        if (soundSystem.playing(MEDIA_ID)) {
            return getCurrentMusicTime() / getMusicLength(currentMusic);
        }
        return 0;
    }

    public VorbisFile getMusicVorbisFile(@Nonnull Music music) {
        try {
            return new VorbisFile(MUSIC_FILES_FOLDER + File.separator + music.getFileName());
        } catch (JOrbisException e) {
            AcademyCraft.log.error("Failed to get music vorbis file: {}", music.getFilePath(), e);
            throw new RuntimeException(e);
        }
    }

    public float getMusicLength(@Nonnull Music music) {
        return getMusicVorbisFile(music).time_total(-1);
    }

    private boolean isValidFile(URL url) {
        try (InputStream inputStream = url.openStream()) {
            return inputStream != null;
        } catch (IOException e) {
            return false;
        }
    }

    private void createDirectoryIfNotExists(Path... paths) {
        for (Path path : paths) {
            if (!path.toFile().exists()) {
                AcademyCraft.log.info("Path {} does not exist. Creating one...", path);
                if (path.toFile().mkdirs()) {
                    AcademyCraft.log.info("Path {} created.", path);
                } else {
                    AcademyCraft.log.error("Path {} could not be created.", path);
                }
            }
        }
    }
}