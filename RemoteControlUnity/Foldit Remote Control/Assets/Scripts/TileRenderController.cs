using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Collections.Generic;

public class TileRenderController : MonoBehaviour {

    public GameObject RenderTile;
    public RectTransform Panel;
    private int Width;
    private int Height;
    const int TILE_SIZE = 16;
    private int TilesWide;
    private int TilesTall;
    private int TileCount;

    private TileRenderer[] Tiles;

    private List<TileRenderer> ChangedTiles;

	// Use this for initialization
	void Start () {
        Rect panelRec = Panel.rect;
        Width = (int) panelRec.width;
        Height = (int) panelRec.height;
        TilesWide = Width / TILE_SIZE;
        TilesTall = Height / TILE_SIZE;

        TileCount = TilesWide * TilesTall;
        Tiles = new TileRenderer[TileCount];
        ChangedTiles = new List<TileRenderer>();

        for(int i = 0; i < TileCount; i++)
        {
            GameObject tile = Instantiate<GameObject>(RenderTile);
            tile.name = "Tile (" + i % TilesWide + ", " + i / TilesWide + ")";
            tile.transform.SetParent(Panel);
            TileRenderer rend = tile.GetComponent<TileRenderer>();
            Tiles[i] = rend;
        }
    }

    public void SetTile(int x, int y, Color32[] colors, bool lores)
    {
        int index = y * TilesWide + x;
        //TODO: Does this make more sense on the server code? also efficency?
        Tiles[index].ReadyTile(FlipArray.Flip(colors, TILE_SIZE, TILE_SIZE));
        ChangedTiles.Add(Tiles[index]);
    }

    public void SetTile(int x, int y, Color32 color)
    {
        int index = y * TilesWide + x;
        Tiles[index].ReadyTile(color);
        ChangedTiles.Add(Tiles[index]);
    }

    public void Flush()
    {

        foreach(TileRenderer tile in ChangedTiles)
        {
            tile.Flush();
        }

        ChangedTiles.Clear();
    }
}
