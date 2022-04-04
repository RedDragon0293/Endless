package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.optifine.config.Config;

public class VertexBufferUploader extends WorldVertexBufferUploader
{
    private VertexBuffer vertexBuffer = null;

    public void draw(WorldRenderer worldRenderer) {
        if (worldRenderer.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
            worldRenderer.quadsToTriangles();
            this.vertexBuffer.setDrawMode(worldRenderer.getDrawMode());
        }

        this.vertexBuffer.func_181722_a(worldRenderer.getByteBuffer());
        worldRenderer.reset();
    }

    public void setVertexBuffer(VertexBuffer vertexBufferIn)
    {
        this.vertexBuffer = vertexBufferIn;
    }
}
