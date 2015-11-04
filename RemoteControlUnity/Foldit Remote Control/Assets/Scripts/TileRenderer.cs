using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class TileRenderer : MonoBehaviour {

    public RawImage Display;
    public Texture2D ActiveTexture;
    public Texture2D NextTexture;

    const int SIZE = 16;
    
    // Use this for initialization
	void Start () {
        ActiveTexture = new Texture2D(SIZE, SIZE);
	}

    public void ReadyTile(Color32[] colors)
    {
        if (NextTexture != null)
        {
            Destroy(NextTexture);
        }

        NextTexture = new Texture2D(SIZE, SIZE);
        NextTexture.SetPixels32(colors);
        NextTexture.Apply();
    }

    public void ReadyTile(Color32 color)
    {
        if(NextTexture != null)
        {
            Destroy(NextTexture);
        }

        NextTexture = new Texture2D(1, 1);
        NextTexture.SetPixel(0, 0, color);
        NextTexture.Apply();
    }

    public void Flush()
    {
        if (NextTexture != null)
        {
            GameObject.Destroy(ActiveTexture);
            ActiveTexture = NextTexture;
            NextTexture = null;
            Display.texture = ActiveTexture;
        }
    }
}
