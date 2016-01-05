using UnityEngine;
using System.Collections;
using TouchScript.Gestures;
using System;
using UnityEngine.UI;
using UnityEngine.EventSystems;
using TouchScript.Gestures.Simple;

// Ashley Sullivan
// Sean Moss

// 06 December 2015

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
    }

    // Update is called once per frame
    void Update () {
		// Checks for zooming via pinching.
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
			// Practical purpose: Determine direction of zoom
			float deltaMagnitudeDiff = prevTouchDeltaMag - touchDeltaMag;

			netConScript.Zoom(deltaMagnitudeDiff > 0);

		}
		
	}

    public void OnPointerDown(PointerEventData eventData)
    {
		netConScript.Tap(true, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
	}

    public void OnPointerUp(PointerEventData eventData)
    {
		netConScript.Tap(false, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
    }

	public void OnDrag(PointerEventData eventData)
    {
		if (Input.touchCount < 2) { // Either mouse input or single drag
			netConScript.MouseMove ((int)Input.mousePosition.x, (int)Input.mousePosition.y);
		} else { // Pan around the ... panel
			Vector3 delta = eventData.delta;
			trc.dragPanel (new Vector2 (delta.x, delta.y));
		}
	}
}

