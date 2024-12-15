package cn.lambdalib2.vis.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author WeAthFolD
 */
public class AnimationList extends Animation {
    
    private List<Animation> anims = new ArrayList<>();
    
    public AnimationList(Animation ..._anims) {
        anims.addAll(Arrays.asList(_anims));
    }
    
    public AnimationList(Collection<Animation> _anims) {
        anims.addAll(_anims);
    }

    @Override
    public void perform(double timePoint) {
        for(Animation a : anims)
            a.perform(timePoint);
    }
    
}
