package cn.academy.api.ability;

import cn.academy.ACConfig;
import cn.academy.Resources;
import cn.academy.internal.ability.Controllable;
import cn.academy.internal.ability.context.*;
import cn.academy.internal.ability.context.Context.Status;
import cn.academy.internal.ability.develop.DeveloperType;
import cn.academy.internal.ability.develop.condition.DevConditionDep;
import cn.academy.internal.ability.develop.condition.DevConditionDeveloperType;
import cn.academy.internal.ability.develop.condition.DevConditionLevel;
import cn.academy.internal.ability.develop.condition.IDevCondition;
import cn.academy.internal.datapart.AbilityData;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.typesafe.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Skill is the basic control unit of an ESPer. A skill is learned through Ability Developer
 * and can be activated/controlled via the Preset system. <br/>
 * A skill must be added into a Category, otherwise its presence is meaningless. <br/>
 * <p>
 * A skill can be specified to not appear in Preset Edit screen. This kind of skills usually serve as 'passive' skills and provide
 * pipeline functions inside to affect the skill damage or other values. <br/>
 * <p>
 * <p>
 * method so that the skill control will take effect.
 *
 * @author WeAthFolD
 */
public abstract class Skill extends Controllable {
    private Category category;

    private final List<IDevCondition> learningConditions = new ArrayList<>();

    private String fullName;

    /**
     * The parent skill of the skill. This is the upper level skill in the Skill Tree UI. If not specified, this skill is the root skill of the type.
     */
    private Skill parent;
    private int id;

    private final String name;
    private ResourceLocation icon;

    private final int level;


    /**
     * The place this skill is at in the Skill Tree UI.
     */
    public float guiX, guiY;

    /**
     * Whether this skill has customized experience definition.
     * If this is set to true, getSkillExp() will be called whenever
     * querying experience of skill.
     */
    public boolean expCustomized = false;

    /**
     * Whether this skill can be controlled (i.e. appear in preset edit ui).
     */
    protected boolean canControl = true;

    /**
     * Whether this skill is a generic skill (Skill used across many categories).
     * If set to true, the logo lookup path and the name lookup path will be changed. (CategoryName="generic")
     */
    protected boolean isGeneric = false;

    /**
     * @param _name   Skill internal name
     * @param atLevel The level at which this skill is in
     */
    public Skill(String _name, int atLevel) {
        name = _name;
        level = atLevel;
        fullName = "<unassigned>." + name;

        addDevCondition(new DevConditionLevel());
    }

    final void addedSkill(Category _category, int id) {
        category = _category;
        this.id = id;

        icon = initIcon();
        fullName = initFullName();

        this.addDevCondition(new DevConditionDeveloperType(getMinimumDeveloperType()));

        initSkill();
    }

    public void setPosition(float x, float y) {
        guiX = x;
        guiY = y;
    }

    /**
     * Callback that is called AFTER the skill is added into the category.
     */
    protected void initSkill() {
    }

    /**
     * Get the id of the skill in the Category.
     */
    public int getID() {
        return id;
    }

    /**
     * Get the level id that this skill is in.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get the direct name of the skill.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the full name of the skill, in format [category].[name].
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Get the display name of the skill.
     */
    public String getDisplayName() {
        return getLocalized("name");
    }

    /**
     * Get the detailed description for the skill, shown in Ability Developer.
     */
    public String getDescription() {
        return getLocalized("desc");
    }

    /**
     * @return The configuration object for this skill.
     */
    public Config getConfig() {
        Config config = ACConfig.instance().getConfig("ac.ability.category").getConfig(getCategoryLocation()).getConfig("skills").getConfig(getName());

        Preconditions.checkNotNull(config);
        return config;
    }

    public float getDamageScale() {
        return getOptionalFloat("damage_scale", 1.0f);
    }

    /**
     * @return Whether the skill is enabled. Disabled skill will NOT appear in Skill Tree, and its learning dependency
     * will be automatically ignored.
     */
    public boolean isEnabled() {
        return getOptionalBool("enabled", true);
    }

    /**
     * @return Whether this skill is permitted to destroy blocks.
     */
    public boolean shouldDestroyBlocks() {
        return getOptionalBool("destroy_blocks", true);
    }

    public float getCPConsumeSpeed() {
        return getOptionalFloat("cp_consume_speed", 1.0f);
    }

    public float getOverloadConsumeSpeed() {
        return getOptionalFloat("overload_consume_speed", 1.0f);
    }

    public float getExpIncrSpeed() {
        return getOptionalFloat("exp_incr_speed", 1.0f);
    }

    private float getOptionalFloat(String path, float fallback) {
        Config cfg = getConfig();
        return cfg.hasPath(path) ? (float) cfg.getDouble(path) : fallback;
    }

    private boolean getOptionalBool(String path, boolean fallback) {
        Config cfg = getConfig();
        return cfg.hasPath(path) ? cfg.getBoolean(path) : fallback;
    }

    public boolean canControl() {
        return isEnabled() && canControl;
    }

    @Override
    public ResourceLocation getHintIcon() {
        return icon;
    }

    @Override
    public String getHintText() {
        return getDisplayName();
    }

    protected String getLocalized(String key) {
        return I18n.format("ac.ability." + getFullName() + "." + key);
    }

    //--- Path init
    protected String getCategoryLocation() {
        return (isGeneric ? "generic" : category.getName());
    }

    /**
     * @return The init full name. Is guaranteed to be called AFTER the Category is assigned.
     */
    protected String initFullName() {
        return getCategoryLocation() + "." + name;
    }

    /**
     * @return The icon of this skill. Is guaranteed to be called AFTER the Category is assigned.
     */
    protected ResourceLocation initIcon() {
        return icon = Resources.getTexture("abilities/" + getCategoryLocation() + "/skills/" + name);
    }

    //--- Hooks

    /**
     * Get called when set expCustomize=true, to query the experience of the skill.
     *
     * @param data
     * @return exp value in [0, 1]
     */
    public float getSkillExp(AbilityData data) {
        return 0.0f;
    }

    @SideOnly(Side.CLIENT)
    protected void activateSingleKey(ClientRuntime rt, int keyID, Function<EntityPlayer, Context> contextSupplier) {
        rt.addKey(keyID, new SingleKeyDelegate(contextSupplier));
    }

    //--- Learning
    public void setParent(Skill skill) {
        setParent(skill, 0.0f);
    }

    public void setParent(Skill skill, float requiredExp) {
        if (parent != null)
            throw new IllegalStateException("You can't set the parent twice!");
        if (skill.isEnabled()) {
            parent = skill;
            this.addDevCondition(new DevConditionDep(parent, requiredExp));
        }
    }

    public Skill getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void addDevCondition(IDevCondition cond) {
        learningConditions.add(cond);
    }

    public void addSkillDep(Skill skill, float exp) {
        if (skill.isEnabled()) {
            addDevCondition(new DevConditionDep(skill, exp));
        }
    }

    /**
     * Returns an immutable list of learning conditions of this skill.
     */
    public List<IDevCondition> getDevConditions() {
        return ImmutableList.copyOf(learningConditions);
    }

    /**
     * @return The stimulation in the developer required in order to learn this skill
     */
    public int getLearningStims() {
        return (int) (3 + level * level * 0.5f);
    }

    /**
     * @return The minimum developer type that this skill will appear on
     */
    public DeveloperType getMinimumDeveloperType() {
        return DeveloperType.PORTABLE;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    public class SingleKeyDelegate extends KeyDelegate {
        private final Function<EntityPlayer, Context> contextSupplier;
        Context context;

        public SingleKeyDelegate(Function<EntityPlayer, Context> contextSupplier) {
            this.contextSupplier = contextSupplier;
        }

        @Override
        public void onKeyDown() {
            if (Minecraft.getMinecraft().player.isSpectator())
                return;
            context = contextSupplier.apply(getPlayer());
            ContextManager.instance.activate(context);

            context.sendToSelf(Context.MSG_KEYDOWN);
        }

        @Override
        public void onKeyTick() {
            if (Minecraft.getMinecraft().player.isSpectator())
                return;
            checkContext();

            if (context != null) {
                context.sendToSelf(Context.MSG_KEYTICK);
            }
        }

        @Override
        public void onKeyUp() {
            if (Minecraft.getMinecraft().player.isSpectator())
                return;

            if (context != null) {
                context.sendToSelf(Context.MSG_KEYUP);
            }

            context = null;
        }

        @Override
        public void onKeyAbort() {
            checkContext();

            if (context != null) {
                context.sendToSelf(Context.MSG_KEYABORT);
            }

            context = null;
        }

        private void checkContext() {
            if (context != null && context.getStatus() == Status.TERMINATED) {
                context = null;
            }
        }

        @Override
        public DelegateState getState() {
            if (context == null) {
                return DelegateState.IDLE;
            } else if (context instanceof IStateProvider) {
                return ((IStateProvider) context).getState();
            } else {
                return DelegateState.ACTIVE;
            }
        }

        @Override
        public ResourceLocation getIcon() {
            return getHintIcon();
        }

        @Override
        public int createID() {
            return 0;
        }

        public Skill getSkill() {
            return Skill.this;
        }
    }
}