package cn.academy.internal.command;

import cn.academy.api.ability.Category;
import cn.academy.api.ability.Skill;
import cn.academy.internal.ability.CategoryManager;
import cn.academy.internal.datapart.AbilityData;
import cn.academy.internal.datapart.CPData;
import cn.academy.internal.datapart.CooldownData;
import cn.academy.internal.util.ACCommand;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import cn.lambdalib2.util.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

/**
 * @author WeAthFolD
 */

@NetworkS11nType
public abstract class CommandAIMBase extends ACCommand {
    private static final String MSG_CLEAR_COOLDOWN = "clearcd";

    public static void sendChat(ICommandSender s, String key, Object... pars) {
        PlayerUtils.sendChat(s, key, pars);
    }

    String[] commands = {
        "help", "cat", "catlist", 
        "learn", "learn_all", "reset",
        "learned", "skills", "fullcp",
        "level", "exp", "cd_clear", "maxout"
    };

    public CommandAIMBase(String name) {
        super(name);
    }
    
    protected void matchCommands(ICommandSender ics, EntityPlayer player, String[] pars) {
        AbilityData aData = AbilityData.get(player);
        switch(pars[0]) {

        case "cat": {
            if(pars.length == 1) {
                sendChat(ics, getLoc("curcat"), aData.hasCategory() ?
                        aData.getCategory().getDisplayName() :
                        I18n.translateToLocal(getLoc("nonecat")));
                return;
            } else if(pars.length == 2) {
                String catName = pars[1];
                Category cat = CategoryManager.INSTANCE.getCategory(catName);
                if(cat != null) {
                    aData.setCategory(cat);
                    sendChat(ics, locSuccessful());
                } else {
                    sendChat(ics, getLoc("nocat"));
                }
                return;
            }
            break;    
        }

        case "catlist": {
            sendChat(ics, getLoc("cats"));
            List<Category> catList = CategoryManager.INSTANCE.getCategories();
            for(int i = 0; i < catList.size(); ++i) {
                Category cat = catList.get(i);
                sendChat(ics, "#" + i + " " + cat.getName() + ": " + cat.getDisplayName());
            }
            break;
        }
        
        case "learn": {
            if (aData.hasCategory()) {
                Skill s = tryParseSkill(aData.getCategory(), pars[1]);
                if(s == null) {
                    sendChat(ics, getLoc("noskill"));
                } else {
                    aData.learnSkill(s);
                }
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }
        
        case "unlearn": {
            if (aData.hasCategory()) {
                Category cat = aData.getCategory();
                Skill s = tryParseSkill(cat, pars[1]);
                if(s == null) {
                    sendChat(ics, getLoc("noskill"));
                } else {
                    aData.setSkillLearnState(s, false);
                }
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }
        
        case "learn_all": {
            if (aData.hasCategory()) {
                aData.learnAllSkills();
                sendChat(ics, locSuccessful());
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }
        
        case "reset": {
            aData.setCategory(null);
            sendChat(ics, locSuccessful());
            return;
        }
        
        case "learned": {
            StringBuilder sb = new StringBuilder();
            
            boolean begin = true;
            for(Skill s : aData.getLearnedSkillList()) {
                sb.append(begin ? "" : ", ").append(s.getName());
                begin = false;
            }
            
            sendChat(ics, getLoc("learned.format"), sb.toString());
            return;
        }
        
        case "skills": {
            if (aData.hasCategory()) {
                Category cat = aData.getCategory();
                for(Skill s : cat.getSkillList()) {
                    sendChat(ics, "#" + s.getID() + " " + s.getName() + ": " + s.getDisplayName());
                }
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }
        
        case "level": {
            
            if(pars.length == 1) {
                sendChat(ics, "" + aData.getLevel());
            } else {
                try
                {
                    int lv = Integer.valueOf(pars[1]);
                    if(lv > 0 && lv <= 5) {
                        aData.setLevel(lv);
                        sendChat(ics, locSuccessful());
                    } else {
                        sendChat(ics, this.getLoc("outofrange"), 1, 5);
                    }
                }
                catch(NumberFormatException e)
                {
                    sendChat(ics, this.getLoc("invalidnum"), pars[1]);
                }

            }
            
            return;
        }
        
        case "fullcp": {

            if (aData.hasCategory()) {
                CPData cpData = CPData.get(player);
                cpData.setCP(cpData.getMaxCP());
                cpData.setOverload(0);
                sendChat(ics, locSuccessful());
                return;
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }
        
        case "exp": {

            if (aData.hasCategory()) {
                Category cat = aData.getCategory();

                if (pars.length == 1) {
                    sendChat(ics, this.locInvalid());
                } else {
                    Skill skill = tryParseSkill(cat, pars[1]);
                    if(skill == null) {
                        sendChat(ics, getLoc("noskill"));
                    } else {
                        if(pars.length == 2) {
                            sendChat(ics, this.getLoc("curexp"), skill.getDisplayName(), aData.getSkillExp(skill) * 100);
                        } else if(pars.length == 3) {
                            Float exp = tryParseFloat(pars[2]);
                            if(exp < 0 || exp > 1) {
                                sendChat(ics, this.getLoc("outofrange"), 0.0f, 1.0f);
                            } else {
                                aData.setSkillExp(skill, exp);
                                sendChat(ics, this.locSuccessful());
                            }
                        } else {
                            sendChat(ics, this.locInvalid());
                        }
                    }
                }
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }

        case "cd_clear": {

            if (aData.hasCategory()) {
                CooldownData.of(player).clear();
                sendChat(ics, locSuccessful());
                return;
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }

        case "maxout": {

            if (aData.hasCategory()) {
                aData.maxOutLevelProgress();
                sendChat(ics, locSuccessful());
                return;
            } else {
                sendChat(ics, getLoc("nonecathint"));
            }
            return;
        }

        default: {
            sendChat(ics, getLoc("nocomm"));
            return;
        }
        }
    }
    
    private Integer tryParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return null;
        }
    }
    
    private Float tryParseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch(NumberFormatException e) {
            return null;
        }
    }
    
    private Skill tryParseSkill(Category cat, String str) {
        if(cat == null)
            return null;
        Integer i = tryParseInt(str);
        if(i != null)
            return cat.getSkill(i);
        return cat.getSkill(str);
    }
}