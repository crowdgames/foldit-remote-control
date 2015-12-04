using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Collections.Generic;
using System;

// File authority: Martha
// setPanelAndTextureSize: Gregory

public class TileRenderController : MonoBehaviour {

    public RectTransform MyCanvas;
    public RectTransform MyPanel;
    public RawImage Display;
    public Texture2D Texture;

    public int Width { get; private set; }
    public int Height { get; private set; }

    //The fraction of the screen size that the panel covers after the black borders are applied
    public float PanelWidthCovered { get; private set; }
    public float PanelHeightCovered { get; private set; }

    public const int TILE_SIZE = 16;
    public const int TILE_SIZE_SQUARED = TILE_SIZE * TILE_SIZE;
    public const int LORES_TILE_SIZE = 32;

    //16:9 screen size which should fit most modern computers and have room to spare
    //we give Foldit a different pixel resolution than the device so that the UI is small enough to interact with the protein
    public const float TARGET_SERVER_WINDOW_WIDTH = 1024;
    public const float TARGET_SERVER_WINDOW_HEIGHT = TARGET_SERVER_WINDOW_WIDTH * 9 / 16;

    private List<TileInfo> NewTiles;

    public NetworkConScript networkConnection;

    public bool lowres { get; private set; }

    void Start () {
        setPanelAndTextureSize();

        lowres = false;

        NewTiles = new List<TileInfo>();
        Display.texture = Texture;
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
        NewTiles.Add(new TileInfo(x, y, colors));
    }

    // create a tile info for a single color tile
    public void SetTile(int x, int y, Color32 color)
    {
        NewTiles.Add(new TileInfo(x, y, color));
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
    public void resetTexture()
    {
        Color32[] transparent = new Color32[Width * Height];
        for(int x = 0; x < Width*Height; x++)
        {
            transparent[x] = Color.clear;
        }
        Texture.SetPixels32(0, 0, Width, Height, transparent);
        Texture.Apply();
    }
    // Draw a TileInfo to the texture
    private void drawTile(TileInfo tile)
    {
        Texture.SetPixels32(tile.x, tile.y, TILE_SIZE, TILE_SIZE, tile.colors);
    }

    // Set the size of the panel and the texture
    private void setPanelAndTextureSize()
    {
        //When we tell Foldit what size the server window should be, we use a different pixel size
        //than that of the device. We want to maintain the device's aspect ratio while also making
        //the window as big as possible within a target resolution.
        float destWidth = TARGET_SERVER_WINDOW_WIDTH,
            destHeight = TARGET_SERVER_WINDOW_HEIGHT;

        //Compare the device's aspect ratio to the target aspect ratio (currently 16:9)
        float targetAspectRatio = TARGET_SERVER_WINDOW_WIDTH / TARGET_SERVER_WINDOW_HEIGHT;
        float actualAspectRatio = (float)(Screen.width) / (float)(Screen.height);
        //wider- shrink the target height to match the ratio
        if (actualAspectRatio > targetAspectRatio)
            destHeight *= targetAspectRatio / actualAspectRatio;
        //narrower- shrink the width
        else
            destWidth *= actualAspectRatio / targetAspectRatio;

        //Now we need to reduce that to a multiple of 32
        //This is the size we send to Foldit and the size of our texture
        Width = (int)(destWidth) / LORES_TILE_SIZE * LORES_TILE_SIZE;
        Height = (int)(destHeight) / LORES_TILE_SIZE * LORES_TILE_SIZE;
        Texture = new Texture2D(Width, Height);

        //Now we need to resize the panel.
        //We want borders around the edge to compensate for the pixels we chopped off by rounding to 32.
        PanelWidthCovered = Width / destWidth;
        PanelHeightCovered = Height / destHeight;

        //Base the panel size off of the canvas size
        float panelWidth = MyCanvas.rect.width * PanelWidthCovered;
        float panelHeight = MyCanvas.rect.height * PanelHeightCovered;
        MyPanel.sizeDelta = new Vector2(panelWidth, panelHeight);
    }
}
