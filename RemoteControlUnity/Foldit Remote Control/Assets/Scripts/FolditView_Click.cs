using UnityEngine;
using System.Collections;
using TouchScript.Gestures;
using System;
using UnityEngine.UI;
using UnityEngine.EventSystems;

public class FolditView_Click : MonoBehaviour, IPointerClickHandler {
	// Drag to initialize
	public GameObject networkCon;
	// Use this for initialization
	void Start () {
		//GetComponent<TapGesture>().Tapped += clickInFoldit;
	}
	
	// Update is called once per frame
	void Update () {
	
	}



	/// <summary>
	/// React to clicks.
	/// </summary>
	public virtual void OnPointerClick(PointerEventData eventData)
	{
//		Vector2 position;
//		RectTransformUtility.ScreenPointToLocalPointInRectangle(
//			GetComponent<RectTransform>(), 
//		    Input.mousePosition, 
//		    Camera.main, 
//		    out position);
		Vector2 localpos = Input.mousePosition;
		//localpos = Vector2.Scale(localpos, new Vector2(1.375f, 1.32f));
		Debug.Log ("Local point: " + localpos);
		networkCon.GetComponent<NetworkConScript>().Tap(true, 
		                                                Mathf.FloorToInt(localpos.x), 
		                                                Mathf.FloorToInt(localpos.y));
	}

	// Sends a click for the correct coordinates to Foldit
	/**public void clickInFoldit(object sender, EventArgs e)
	{
		Vector2 position = (sender as TapGesture).ScreenPosition;
		Debug.Log (position);
	}**/

}

