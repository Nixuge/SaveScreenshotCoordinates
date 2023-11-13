package me.nixuge.savescreenshotcoordinates.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ScreenShotHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mixin(ScreenShotHelper.class)
public class MixinScreenshot {
    @Inject(method = "saveScreenshot(Ljava/io/File;Ljava/lang/String;IILnet/minecraft/client/shader/Framebuffer;)Lnet/minecraft/util/IChatComponent;", at = @At(value = "INVOKE", target = "Ljavax/imageio/ImageIO;write(Ljava/awt/image/RenderedImage;Ljava/lang/String;Ljava/io/File;)Z"))
    private static void saveToFile(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer, CallbackInfoReturnable<IChatComponent> cir) {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        String coordinatesString = p.posX + " " + p.posY + " " + p.posZ + ", " + p.rotationYaw + " " + p.rotationPitch;

        String screenSizeString = "(" + buffer.framebufferTextureWidth + "x" + buffer.framebufferTextureHeight + ")";

        File screenshotDirectory = new File(gameDirectory, "screenshots");
        File txtFile = new File(screenshotDirectory, "coordinates.txt");


        try {
            if (!txtFile.isFile())
                txtFile.createNewFile();

            FileWriter fw = new FileWriter(txtFile, true);
            // Note: This will output null if screenshotName is null (always the case by default,
            // meaning this thing is for now only useful with the ScreenshotWorldName mod).
            fw.append(screenshotName).append(" || ").append(coordinatesString).append(" ").append(screenSizeString).append("\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
