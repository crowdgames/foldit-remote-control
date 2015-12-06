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
		if (Input.touchCount == 2) {	// Store both touches.
			Touch touchZero = Input.GetTouch (0);
			Touch touchOne = Input.GetTouch (1);
		
			// Find the position in the previous frame of each touch.
			Vector2 touchZeroPrevPos = touchZero.position - touchZero.deltaPosition;
			Vector2 touchOnePrevPos = touchOne.position - touchOne.deltaPosition;
		
			// Find the magnitude of the vector (the distance) between the touches in each frame.
			float prevTouchDeltaMag = (touchZeroPrevPos - touchOnePrevPos).magnitude;
			float touchDeltaMag = (touchZero.position - touchOne.position).magnitude;
		
			// Find the difference in the distances between each frame.
			float deltaMagnitudeDiff = prevTouchDeltaMag - touchDeltaMag;

			netConScript.Zoom(deltaMagnitudeDiff > 0);

		}
		
	}
	private void releasedHandler(object sender, EventArgs e)
		
	{
		Debug.Log ("Release");
		netConScript.Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

    private void pressedHandler(object sender, EventArgs e)
    {
		Debug.Log ("Press");
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
		Debug.Log ("Pan");
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

	//public void OnDrag(PointerEventData eventData) {
	//		Vector3 delta = eventData.delta;
	//		trc.dragPanel (new Vector2 (delta.x, delta.y));
	//	}


    public void OnDrag(PointerEventData eventData)
    {
		if (Input.touchCount < 2) { // Either mouse input or single drag
			Vector2 adjusted = trc.factorOutZoom (new Vector2 ((int)Input.mousePosition.x, (int)Input.mousePosition.y));
			netConScript.MouseMove ((int)adjusted.x, (int)adjusted.y);
		} else {
			Vector3 delta = eventData.delta;
			trc.dragPanel (new Vector2 (delta.x, delta.y));
		}
	}
}

