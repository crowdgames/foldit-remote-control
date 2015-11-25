using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

// Developed by Courtney Toder and Elizabeth Renn

public class DragMenu : MonoBehaviour, IDragHandler, IEndDragHandler {
	private const float minAnchorXPulledOut = -.25f;
	private const float minAnchorXPulledIn = 0f;
	private const float maxAnchorXPulledOut = .05f;
	private const float maxAnchorXPulledIn = .3f;

	#region IDragHandler implementation

	public void OnDrag (PointerEventData eventData)
	{
		RectTransform trans = (RectTransform)transform;
		float minX = maxAnchorXPulledIn * Screen.width;
		float maxX = maxAnchorXPulledOut * Screen.width;
		float percentage = (Input.mousePosition.x - minX) / (maxX - minX);

		trans.anchorMin = new Vector2 (Mathf.Lerp (minAnchorXPulledIn, minAnchorXPulledOut, percentage), 0);
		trans.anchorMax = new Vector2 (Mathf.Lerp (maxAnchorXPulledIn, maxAnchorXPulledOut, percentage), 1);

	}

	#endregion

	#region IEndDragHandler implementation
	public void OnEndDrag (PointerEventData eventData)
	{
		// Snap to the closest side if we aren't already there.
		RectTransform trans = (RectTransform)transform;
		float minX = maxAnchorXPulledIn * Screen.width;
		float maxX = maxAnchorXPulledOut * Screen.width;
		float percentage = (Input.mousePosition.x - minX) / (maxX - minX);

		// if the percentage is within 50% snap to the left, otherwise snap to the right
		if (percentage <= .5) {
			trans.anchorMin = new Vector2 (minAnchorXPulledIn, 0);
			trans.anchorMax = new Vector2 (maxAnchorXPulledIn, 1);
		} else {
			trans.anchorMin = new Vector2 (minAnchorXPulledOut, 0);
			trans.anchorMax = new Vector2 (maxAnchorXPulledOut, 1);
		}
	}
	#endregion
}