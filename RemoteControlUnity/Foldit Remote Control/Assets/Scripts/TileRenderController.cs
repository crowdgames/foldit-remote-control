using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Collections.Generic;
using System;

// File authority: Martha

public class TileRenderController : MonoBehaviour {

    public RectTransform MyCanvas;
    public RectTransform MyPanel;
    public RawImage Display;
    public Texture2D Texture;

    public int Width { get; private set; }
    public int Height { get; private set; }

	public const int TILE_SIZE = 16;
	public const int TILE_SIZE_SQUARED = TILE_SIZE * TILE_SIZE;

    private List<TileInfo> NewTiles;

	public NetworkConScript networkConnection;

    public bool lowres { get; private set; }

	void Start () {
        setPanelSize();

        Rect panelRec = MyPanel.rect;
        Width = (int) panelRec.width;
        Height = (int) panelRec.height;

        lowres = false;

        Texture = new Texture2D(Width, Height);
        NewTiles = new List<TileInfo>();
        Display.texture = Texture;

        networkConnection.StartWithTileRenderController(this);
    }

    // Used to change if the program is working with lowres settings or not
    public void setIfLowRes(bool val)
    {
        if(val != lowres)
        {
            lowres = val;

            Texture2D oldTexture = Texture;

            if(lowres)
            {
                Texture = new Texture2D(Width / 2, Height / 2);
            } else
            {
                Texture = new Texture2D(Width, Height);
            }

            Display.texture = Texture;

            Destroy(oldTexture);
        }
    }

    // Create a tile info for a tile with an array of colors
    public void SetTile(int x, int y, Color32[] colors)
    {
        if (lowres)
        {
            NewTiles.Add(new TileInfo(x / 2, y / 2, colors));
        } else
        {
            NewTiles.Add(new TileInfo(x, y, colors));
        }
    }

    // create a tile info for a single color tile
    public void SetTile(int x, int y, Color32 color)
    {
        if (lowres)
        {
            NewTiles.Add(new TileInfo(x / 2, y / 2, color));
        } else {
            NewTiles.Add(new TileInfo(x, y, color));
        }
    }

    // Draw all tiles waiting in NewTiles
    public void Flush()
    {
        foreach(TileInfo tile in NewTiles)
        {
            drawTile(tile);
        }
        Texture.Apply();
        NewTiles.Clear();
    }

    // Draw a TileInfo to the texture
    private void drawTile(TileInfo tile)
    {
        Texture.SetPixels32(tile.x, tile.y, TILE_SIZE, TILE_SIZE, tile.colors);
    }

    // Set the panel size to a multiple of 16
    private void setPanelSize()
    {
        float width = MyCanvas.rect.width;
        float height = MyCanvas.rect.height;
        int widthInt = Mathf.FloorToInt(width / 16f) * 16;
        int heightInt = Mathf.FloorToInt(height / 16f) * 16;
        MyPanel.sizeDelta = new Vector2(widthInt, heightInt);
    }
}
