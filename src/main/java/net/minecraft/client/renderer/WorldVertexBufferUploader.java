package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.optifine.config.Config;
import net.optifine.shaders.SVertexBuilder;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.List;

public class WorldVertexBufferUploader {
    @SuppressWarnings("incomplete-switch")
    public void draw(WorldRenderer worldRenderer) {
        if (worldRenderer.getVertexCount() > 0) {
            if (worldRenderer.getDrawMode() == GL11.GL_QUADS && Config.isQuadsToTriangles()) {
                worldRenderer.quadsToTriangles();
            }

            VertexFormat vertexformat = worldRenderer.getVertexFormat();
            int i = vertexformat.getNextOffset();
            ByteBuffer bytebuffer = worldRenderer.getByteBuffer();
            List<VertexFormatElement> list = vertexformat.getElements();

            for (int j = 0; j < list.size(); ++j) {
                VertexFormatElement vertexformatelement = list.get(j);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();

                int k = vertexformatelement.getType().getGlConstant();
                int l = vertexformatelement.getIndex();
                bytebuffer.position(vertexformat.getOffset(j));

                switch (vertexformatelement$enumusage) {
                    case POSITION:
                        GL11.glVertexPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + l);
                        GL11.glTexCoordPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GL11.glColorPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                        break;

                    case NORMAL:
                        GL11.glNormalPointer(k, i, bytebuffer);
                        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            if (worldRenderer.isMultiTexture()) {
                worldRenderer.drawMultiTexture();
            } else if (Config.isShaders()) {
                SVertexBuilder.drawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount(), worldRenderer);
            } else {
                GL11.glDrawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount());
            }

            int j1 = 0;

            for (int k1 = list.size(); j1 < k1; ++j1) {
                VertexFormatElement vertexformatelement1 = list.get(j1);
                VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();

                int i1 = vertexformatelement1.getIndex();

                switch (vertexformatelement$enumusage1) {
                    case POSITION:
                        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
                        break;

                    case UV:
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + i1);
                        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;

                    case COLOR:
                        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                        GlStateManager.resetColor();
                        break;

                    case NORMAL:
                        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }
        }

        worldRenderer.reset();
    }
}
