using UnityEngine;
using System.Collections;

public class TestRendering : MonoBehaviour {

    public TileRenderController RendControl;

    public bool doneState = false;
    public int state = 0;
    public int count = 0;
    public int changeAt = 60;

    const int TILE_SIZE = 16;

    // Use this for initialization
    void Start () {
	
	}
	
	// Update is called once per frame
	void FixedUpdate () {
	    if (!doneState && (state == 0))
        {
            RendControl.SetTile(4, 4, Color.blue);

            Color32[] colors = new Color32[TILE_SIZE * TILE_SIZE];
            for (int i = 0; i < TILE_SIZE; i++)
            {
                for(int j = 0; j < TILE_SIZE; j++)
                {
                    colors[i + (j * TILE_SIZE)] = new Color32((byte)(((float)i / TILE_SIZE) * 255), (byte) (((float)j / TILE_SIZE) * 255), 0, 255);
                }
            }

            RendControl.SetTile(6, 4, colors, false);
            RendControl.Flush();
            doneState = true;
        } else if(!doneState && (state == 1))
        {
            RendControl.SetTile(4, 4, Color.red);

            Color32[] colors = new Color32[TILE_SIZE * TILE_SIZE];
            for(int i = 0; i < TILE_SIZE; i++)
            {
                for(int j = 0; j < TILE_SIZE; j++)
                {
                    colors[i + (j * TILE_SIZE)] = new Color32((byte)(((float)i / TILE_SIZE) * 255), (byte)(((float)j / TILE_SIZE) * 255), 255, 255);
                }
            }

            RendControl.SetTile(6, 4, colors, false);

            RendControl.Flush();
            doneState = true;
        } else if(!doneState && (state == 2))
        {
            RendControl.SetTile(4, 4, Color.green);

            Color32[] colors = new Color32[TILE_SIZE * TILE_SIZE];
            for(int i = 0; i < TILE_SIZE; i++)
            {
                for(int j = 0; j < TILE_SIZE; j++)
                {
                    colors[i + (j * TILE_SIZE)] = new Color32((byte)(((float)i / TILE_SIZE) * 255), 255, (byte)(((float)j / TILE_SIZE) * 255), 255);
                }
            }

            RendControl.SetTile(6, 4, colors, false);

            RendControl.Flush();
            doneState = true;
        }

        if (doneState)
        {
            count += 1;
            if (count > changeAt)
            {
                count = 0;
                state = (state + 1) % 3;
                doneState = false;
            }
        }
	}
}
