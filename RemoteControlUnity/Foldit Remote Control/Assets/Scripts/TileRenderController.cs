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

    private float standardWidth;
    private float standardHeight;
    private const float MAX_ZOOM = 3f;
    private float currentZoomPercent = 0f;

    public const int TILE_SIZE = 16;
    public const int TILE_SIZE_SQUARED = TILE_SIZE * TILE_SIZE;
    public const int LORES_TILE_SIZE = 32;

    //16:9 screen size which should fit most modern computers and have room to spare
    //we give Foldit a different pixel resolution than the device so that the UI is small enough to interact with the protein
    public const float TARGET_SERVER_WINDOW_WIDTH = 1024;
    public const float TARGET_SERVER_WINDOW_HEIGHT = TARGET_SERVER_WINDOW_WIDTH * 9 / 16;

    public NetworkConScript networkConnection;

    public bool lowres { get; private set; }

    void Start () {
Debug.Log("Canvas is " + MyCanvas.rect.width + "x" + MyCanvas.rect.height);
        setPanelAndTextureSize();

        lowres = false;

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

    // Update the texture with the given TILE_SIZExTILE_SIZE block of colors
    public void SetTile(int x, int y, Color32[] colors)
    {
        Texture.SetPixels32(x, y, TILE_SIZE, TILE_SIZE, colors);
    }

    // Commit all changes to the texture
    public void Flush()
    {
        Texture.Apply();
    }

    // Set the size of the panel and the texture
    private void setPanelAndTextureSize()
    {
        Debug.Log("Screen is " + Screen.width + "x" + Screen.height);
        //When we tell Foldit what size the server window should be, we use a different pixel size
        //than that of the device. We want to maintain the device's aspect ratio while also making
        //the window as big as possible within a target resolution.
        float destWidth = TARGET_SERVER_WINDOW_WIDTH,
            destHeight = TARGET_SERVER_WINDOW_HEIGHT;

        //Compare the device's aspect ratio to the target aspect ratio (currently 16:9)
        float targetAspectRatio = TARGET_SERVER_WINDOW_WIDTH / TARGET_SERVER_WINDOW_HEIGHT;
        float actualAspectRatio = (float)(Screen.width) / (float)(Screen.height);

        //wider- shrink the target height to match the ratio
        if(actualAspectRatio > targetAspectRatio)
        {
            destHeight *= targetAspectRatio / actualAspectRatio;
        } else {
            //narrower- shrink the width
            destWidth *= actualAspectRatio / targetAspectRatio;
            Debug.Log("Target Foldit screen size: " + destWidth + "x" + destHeight);
        }

        //Now we need to reduce that to a multiple of 32
        //This is the size we send to Foldit and the size of our texture
        Width = (int)(destWidth) / LORES_TILE_SIZE * LORES_TILE_SIZE;
        Height = (int)(destHeight) / LORES_TILE_SIZE * LORES_TILE_SIZE;
        Texture = new Texture2D(Width, Height);
        Debug.Log("Resulting texture size: " + Width + "x" + Height);

        //Now we need to resize the panel.
        //We want borders around the edge to compensate for the pixels we chopped off by rounding to 32.
        PanelWidthCovered = Width / destWidth;
        PanelHeightCovered = Height / destHeight;

        //Base the panel size off of the canvas size
        float panelWidth = MyCanvas.rect.width * PanelWidthCovered;
        float panelHeight = MyCanvas.rect.height * PanelHeightCovered;
        Debug.Log("Panel size: " + panelWidth + "x" + panelHeight);
        MyPanel.sizeDelta = new Vector2(panelWidth, panelHeight);
    }

    public void updateZoom(float zoomPercent)
    {
        Debug.Log("zoom precent:" + zoomPercent);
        currentZoomPercent = zoomPercent;
        float ZoomCurrent = Mathf.Lerp(1f, MAX_ZOOM, zoomPercent);
        MyPanel.sizeDelta = new Vector2(standardWidth * ZoomCurrent, standardHeight * ZoomCurrent);
    }
}
