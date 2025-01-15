package cn.academy.internal.client.ui;

import cn.academy.Resources;
import cn.academy.Tags;
import cn.academy.internal.client.misc.Music;
import cn.academy.internal.client.misc.MusicSystem;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.WidgetContainer;
import cn.lambdalib2.cgui.component.*;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import net.minecraft.util.ResourceLocation;

public class MusicPlayerGui extends CGuiScreen {
    private final WidgetContainer document = CGUIDocument.read(Resources.getGui("music_player"));
    Widget pageMain = document.getWidget("main");
    TextBox title = pageMain.getWidget("title").getComponent(TextBox.class);
    Widget volumeBar = pageMain.getWidget("volume_bar");
    DragBar dragBar = volumeBar.getComponent(DragBar.class);
    Widget stop = pageMain.getWidget("stop");
    Widget pop = pageMain.getWidget("pop");

    public static final ResourceLocation T_PLAY = Resources.getTexture("guis/apps/media_player/play");
    public static final ResourceLocation T_PAUSE = Resources.getTexture("guis/apps/media_player/pause");

    public MusicPlayerGui() {
        initMusicList();
        getGui().addWidget("main", pageMain);
    }

    private void initMusicList() {
        Widget area = pageMain.getWidget("area");
        ElementList list = new ElementList();

        pageMain.getWidget("progress").listen(FrameEvent.class, (widget, event) -> widget.getComponent(ProgressBar.class).progress = MusicSystem.INSTANCE.getCurrentMusicProgress());
        volumeBar.listen(DragBar.DraggedEvent.class, (widget, event) -> MusicSystem.INSTANCE.setVolume(dragBar.getProgress()));
        volumeBar.getComponent(DragBar.class).setProgress(MusicSystem.DEFAULT_VOLUME);
        stop.listen(LeftClickEvent.class, ((widget, event) -> MusicSystem.INSTANCE.stopMusic()));

        pop.listen(LeftClickEvent.class, ((widget, event) -> {
            switch (MusicSystem.getPlayState()) {
                case PLAYING:
                    MusicSystem.INSTANCE.pauseMusic();
                    break;
                case PAUSED:
                    MusicSystem.INSTANCE.continueMusic();
                    break;
                case STOPPED:
                    MusicSystem.INSTANCE.playMusic(MusicSystem.INSTANCE.currentMusic);
                    break;
            }
        }));
        pop.listen(FrameEvent.class, (widget, event) -> {
            if (MusicSystem.getPlayState() == MusicSystem.PlayState.PLAYING){
                pop.getComponent(DrawTexture.class).setTex(T_PAUSE);
            } else {
                pop.getComponent(DrawTexture.class).setTex(T_PLAY);
            }
        });

        for (Music music : MusicSystem.INSTANCE.getMusicList()) {
            Widget musicInfoWidget = pageMain.getWidget("musicInfoWidget").copy();
            musicInfoWidget.transform.doesDraw = true;

            musicInfoWidget.getWidget("icon").getComponent(DrawTexture.class).setTex(new ResourceLocation(Tags.MOD_ID, music.getIconPath()));
            musicInfoWidget.getWidget("title").getComponent(TextBox.class).setContent(music.getName());
            musicInfoWidget.getWidget("desc").getComponent(TextBox.class).setContent(music.getDescription());
            musicInfoWidget.getWidget("time").getComponent(TextBox.class).setContent((int) (MusicSystem.INSTANCE.getMusicLength(music) / 60) + " : " + (int) (MusicSystem.INSTANCE.getMusicLength(music) % 60));

            musicInfoWidget.listen(LeftClickEvent.class, ((widget, event) -> updateMusicInfo(music)));

            list.addWidget(musicInfoWidget);
        }
        area.addComponent(list);
    }

    private void updateMusicInfo(Music music) {
        MusicSystem.INSTANCE.currentMusic = music;
        title.setContent(music.getName());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}