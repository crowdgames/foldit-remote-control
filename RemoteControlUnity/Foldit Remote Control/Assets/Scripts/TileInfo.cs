using UnityEngine;
using System.Collections;

public class TileInfo {

    public int x { get; private set; }
    public int y { get; private set; }
    public Color32[] colors { get; private set; }
    const int SIZE = 16;

    public TileInfo(int inX, int inY, Color32[] inColors)
    {
        x = inX;
        y = inY;
        colors = inColors;
    }

    public TileInfo(int inX, int inY, Color32 inColor)
    {
        x = inX;
        y = inY;
        int colCount = SIZE * SIZE;

        colors = new Color32[colCount];
        for (int i = 0; i < colCount; i++)
        {
            colors[i] = inColor;
        }
    }
}
