using UnityEngine;
using System.Collections;
using TouchScript.Gestures;
using System;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using TouchScript.Gestures.Simple;

// Ashley Sullivan
// Sean Moss

// Clicking in the Foldit view window behavior.

public class FolditView_Click : MonoBehaviour, IPointerDownHandler, IPointerUpHandler, IDragHandler
{
	// Drag to initialize
	public GameObject networkCon;
	public GameObject tileRC;
    private NetworkConScript netConScript;
	private TileRenderController trc;

	

	// Use this for initialization
	void Start () {
        netConScript = networkCon.GetComponent<NetworkConScript>();
		trc = tileRC.GetComponent<TileRenderController> ();

		GetComponent<ScaleGesture> ().Scaled += scaleHandler;
		GetComponent<PanGesture> ().Panned += panHandler;
        GetComponent<ReleaseGesture>().Released += releasedHandler;
        GetComponent<PressGesture>().Pressed += pressedHandler;
    }

    // Update is called once per frame
    void Update () {

	}
    private void releasedHandler(object sender, EventArgs e)

    {
		netConScript.Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    private void pressedHandler(object sender, EventArgs e)
    {
        netConScript.Tap(true, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

	// This is the pinch/nega-pinch to send to Foldit.
	void scaleHandler (object sender, EventArgs e)
	{
		Debug.Log ("Zoom? " + (sender as ScaleGesture).LocalDeltaScale);
		// If scale > 1, we zoomed in, otherwise we zoomed out.
		netConScript.Zoom ((sender as ScaleGesture).LocalDeltaScale > 1);
	}

	// This is the local panning of the panel.
	private void panHandler(object sender, EventArgs e)
    {
		Vector3 delta = (sender as PanGesture).LocalDeltaPosition;
		trc.dragPanel (new Vector2 (delta.x, delta.y));
    }

    public void OnPointerDown(PointerEventData eventData)
    {
		Vector2 adjusted = trc.factorOutZoom (new Vector2((int)Input.mousePosition.x, (int)Input.mousePosition.y));
		netConScript.Tap(true, (int)adjusted.x, (int)adjusted.y);
	}

    public void OnPointerUp(PointerEventData eventData)
    {
		Vector2 adjusted = trc.factorOutZoom (new Vector2((int)Input.mousePosition.x, (int)Input.mousePosition.y));
		netConScript.Tap(false, (int)adjusted.x, (int)adjusted.y);
    }


    public void OnDrag(PointerEventData eventData)
    {
		Vector2 adjusted = trc.factorOutZoom (new Vector2((int)Input.mousePosition.x, (int)Input.mousePosition.y));
		netConScript.MouseMove((int)adjusted.x, (int)adjusted.y);
	}
}

