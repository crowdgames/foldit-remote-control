using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

// Developed by Courtney Toder and Elizabeth Renn

public class DragMenu : MonoBehaviour, IDragHandler, IEndDragHandler {
	private const float dragMenuWidth = 460;
	private const float dragMenuTabWidth = 73;
	private RectTransform canvasRect;
	private RectTransform arrowTrans;
	private Quaternion arrowRot;
	// These values are subject to change based on screen size
	private float minAnchorXPulledOut = 1245;
	private float minAnchorXPulledIn = 1705;
	private float anchoredYPosition = 0;

	void Start () {
		GameObject canvas = GameObject.FindGameObjectWithTag("Canvas");
		canvasRect = canvas.GetComponent<RectTransform> ();
		Debug.Log ("canvas rect is: " + canvasRect.rect.size);
		minAnchorXPulledIn = canvasRect.rect.width - dragMenuTabWidth;
		minAnchorXPulledOut = minAnchorXPulledIn - dragMenuWidth;
		
		RectTransform transMenu = (RectTransform)transform;
		anchoredYPosition = transMenu.anchoredPosition.y;
		transMenu.anchoredPosition = new Vector2 (minAnchorXPulledIn, anchoredYPosition);

		// get the arrow on the menu to flip the image if we need to
		GameObject arrow = GameObject.FindGameObjectWithTag("MenuArrow");
		arrowTrans = arrow.GetComponent<RectTransform> ();
		arrowRot = arrowTrans.rotation;
	}

	#region IDragHandler implementation

	public void OnDrag (PointerEventData eventData)
	{
		RectTransform trans = (RectTransform)transform;
		Vector2 localMouse = new Vector2();
		RectTransformUtility.ScreenPointToLocalPointInRectangle(canvasRect, Input.mousePosition, null, out localMouse);

		// canvas origin is in center so need to add half canvas size to get the origin at far left
		float mousePosX = (localMouse.x + (canvasRect.rect.size.x / 2));
		float percentage = (mousePosX - minAnchorXPulledOut) / (minAnchorXPulledIn - minAnchorXPulledOut);

		trans.anchoredPosition = new Vector2 (Mathf.Lerp (minAnchorXPulledOut, minAnchorXPulledIn, percentage), anchoredYPosition);
	}

	#endregion

	#region IEndDragHandler implementation
	public void OnEndDrag (PointerEventData eventData)
	{
		// Snap to the closest side if we aren't already there.
		RectTransform trans = (RectTransform)transform;
		Vector2 localMouse = new Vector2();
		RectTransformUtility.ScreenPointToLocalPointInRectangle(canvasRect, Input.mousePosition, null, out localMouse);
		
		// canvas origin is in center so need to add half canvas size to get the origin at far left
		float mousePosX = (localMouse.x + (canvasRect.rect.size.x / 2));
		float percentage = (mousePosX - minAnchorXPulledOut) / (minAnchorXPulledIn - minAnchorXPulledOut);

		// if the percentage is within 50% snap to the left, otherwise snap to the right
		if (percentage <= .5) {
			trans.anchoredPosition = new Vector2 (minAnchorXPulledOut, anchoredYPosition);
			arrowRot.y = 180;
		} else {
			trans.anchoredPosition = new Vector2 (minAnchorXPulledIn, anchoredYPosition);
			arrowRot.y = 0;
		}
		// flip the arrow rotation on the menu tab
		arrowTrans.rotation = arrowRot;
	}
	#endregion
}
