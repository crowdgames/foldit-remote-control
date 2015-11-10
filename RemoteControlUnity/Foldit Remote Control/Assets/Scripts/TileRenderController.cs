using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Collections.Generic;
using System;

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

	// Use this for initialization
	void Start () {
        setPanelSize();

        Rect panelRec = MyPanel.rect;
        Width = (int) panelRec.width;
        Height = (int) panelRec.height;

        Texture = new Texture2D(Width, Height);
        NewTiles = new List<TileInfo>();
        Display.texture = Texture;

        networkConnection.StartWithTileRenderController(this);
    }

    public void SetTile(int x, int y, Color32[] colors, bool lores)
    {
        NewTiles.Add(new TileInfo(x, y, colors));
    }

    public void SetTile(int x, int y, Color32 color)
    {
        NewTiles.Add(new TileInfo(x, y, color));
    }

    public void Flush()
    {

        foreach(TileInfo tile in NewTiles)
        {
            drawTile(tile);
        }
        Texture.Apply();
        NewTiles.Clear();
    }

    private void drawTile(TileInfo tile)
    {
        Texture.SetPixels32(tile.x * TILE_SIZE, tile.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, tile.colors);
    }

    private void setPanelSize()
    {
        float width = MyCanvas.rect.width;
        float height = MyCanvas.rect.height;
        int widthInt = Mathf.FloorToInt(width / 16f) * 16;
        int heightInt = Mathf.FloorToInt(height / 16f) * 16;
        MyPanel.sizeDelta = new Vector2(widthInt, heightInt);
    }
}
