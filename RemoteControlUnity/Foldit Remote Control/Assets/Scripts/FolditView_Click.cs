using UnityEngine;
using System.Collections;

// Ashley Sullivan
// Sean Moss
// Thomas Muender

// 11 July 2017

// Clicking in the Foldit view window behavior.

public class FolditView_Click : MonoBehaviour
{
	// Drag to initialize
	public GameObject networkCon;
    private NetworkConScript netConScript;

	private Vector2 scroll = Vector2.zero;

	// Use this for initialization
	void Start () {
        netConScript = networkCon.GetComponent<NetworkConScript>();
    }

    // Update is called once per frame
    void Update () {
		for (int i = 0; i < Input.touchCount; ++i) {
			Touch t = Input.GetTouch (i);
			netConScript.Touch (t.phase, t.fingerId, (int)t.position.x, (int)t.position.y);
		}

		//for debug purposes
		if (Input.GetMouseButtonDown (0)) {
			netConScript.Touch (TouchPhase.Began, 0, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
		} else if (Input.GetMouseButton (0)) {
			netConScript.Touch (TouchPhase.Moved, 0, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
		} else if (Input.GetMouseButtonUp (0)) {
			netConScript.Touch (TouchPhase.Ended, 0, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
		}

		if (Input.GetMouseButtonDown (1)) {
			netConScript.Touch (TouchPhase.Began, 0, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
			netConScript.Touch (TouchPhase.Began, 1, (int)Input.mousePosition.x + 100, (int)Input.mousePosition.y);
		} else if (Input.GetMouseButton (1)) {
			netConScript.Touch (TouchPhase.Moved, 0, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
			netConScript.Touch (TouchPhase.Moved, 1, (int)Input.mousePosition.x + 100, (int)Input.mousePosition.y);
		} else if (Input.GetMouseButtonUp (1)) {
			netConScript.Touch (TouchPhase.Ended, 0, (int)Input.mousePosition.x, (int)Input.mousePosition.y);
			netConScript.Touch (TouchPhase.Ended, 1, (int)Input.mousePosition.x + 100, (int)Input.mousePosition.y);
		}
			
		if (Input.GetAxis ("Mouse ScrollWheel") > 0) {
			netConScript.Zoom (true);
		} else if(Input.GetAxis ("Mouse ScrollWheel") < 0) {
			netConScript.Zoom (false);
		}
	}
}