package cn.academy.internal.ability.develop;

import cn.academy.Resources;
import net.minecraft.util.ResourceLocation;

public enum DeveloperType {
    //--------------------------| syncRate| energy | tps | cps |------------------------------
    PORTABLE(1000, 1.0,  1000000,   10,  1000, "items/developer_portable_empty");

    private final double bandwidth;
    public final double syncRate;
    public final ResourceLocation texture;
    public final double energy, cps;
    public final int tps;
    
    DeveloperType(double _bandwidth, double _syncRate,
                  double _energy, int _tps, double _cps,
                  String _tex) {
        bandwidth = _bandwidth;
        syncRate = _syncRate;
        energy = _energy;
        tps = _tps;
        cps = _cps;

        texture = Resources.getTexture(_tex);
    }
    
    public double getEnergy() {
        return energy;
    }

    // Consumption per stimulation
    public double getCPS() {
        return cps;
    }
    
    public double getBandwidth() {
        return bandwidth;
    }

    // Tick per stimulation
    public int getTPS() {
        return tps;
    }
}