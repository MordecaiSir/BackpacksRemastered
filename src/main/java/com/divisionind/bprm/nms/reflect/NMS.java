/*
 * BackpacksRemastered - remastered version of the popular Backpacks plugin
 * Copyright (C) 2019 Division Industries LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.divisionind.bprm.nms.reflect;

import com.divisionind.bprm.Backpacks;
import com.divisionind.bprm.FakeBackpackViewer;
import org.bukkit.Bukkit;

import java.lang.reflect.Proxy;

public class NMS {

    public static final String VERSION = getVersion();
    public static final String SERVER = "net.minecraft.server." + VERSION + ".";
    public static final String CRAFT = "org.bukkit.craftbukkit." + VERSION + ".";

    public static void initialize() throws ClassNotFoundException, NoSuchMethodException {
        // initialize classes
        for (NMSClass nmsClass : NMSClass.values()) nmsClass.init();

        // init all getters and setters for the various NBTTag data values
        NBTType.COMPOUND.setClassType(NMSClass.NBTBase.getClazz());
        for (NBTType type : NBTType.values()) type.init(NMSClass.NBTTagCompound.getClazz());

        // initialize methods
        for (NMSMethod nmsMethod : NMSMethod.values()) nmsMethod.init();

        // initialize fake viewer using reflection (so that it can support spigot + craftbukkit)
        Backpacks.FAKE_VIEWER = (FakeBackpackViewer) Proxy.newProxyInstance(FakeBackpackViewer.class.getClassLoader(),
                new Class[]{FakeBackpackViewer.class},
                (proxy, method, args) -> {
                    Class type = method.getReturnType();

                    if (type.equals(boolean.class)) return false;
                    if (type.equals(int.class)) return 0;
                    if (type.equals(double.class)) return 0D;
                    if (type.equals(float.class)) return 0F;
                    if (type.equals(long.class)) return 0L;
                    if (type.equals(short.class)) return (short)0;
                    if (type.equals(byte.class)) return (byte)0;

                    return null;
                });
    }

    private static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}