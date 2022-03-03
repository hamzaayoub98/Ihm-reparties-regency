using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Net;
using UnityEngine;
using UnityEngine.Networking;

public class basketGestion : MonoBehaviour
{
    [SerializeField]
    private String currentStep;
    TextMesh basketText;
    private String url;
    void Start()
    {
        currentStep = "RestartEngine";
        url = apiController.url;
        basketText = GameObject.Find("BasketCounter").GetComponent<TextMesh>();
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    void OnCollisionEnter(Collision collision)
    {
        
        Debug.Log("collision detected with " + collision.gameObject.name);
        if (basketText.text != "0")
        {
            StartCoroutine(GetAddVRAntimatiere(url + "addVRAntimatiere"));
            basketText.text = (Int32.Parse(basketText.text) - 1).ToString();
        }
        Destroy(collision.gameObject);
        if (currentStep == "RestartEngine" && basketText.text == "0")
        {
            basketText.color = Color.green;
            StartCoroutine(GetEnginesRdy(url + "no-more-antimatiere"));
            currentStep = "LoadMissile";
        }
    }

    IEnumerator GetAddVRAntimatiere(string url)
    {
        using (UnityWebRequest webRequest = UnityWebRequest.Get(url))
        {
            yield return webRequest.SendWebRequest();
            if (webRequest.result == UnityWebRequest.Result.ConnectionError || webRequest.result == UnityWebRequest.Result.DataProcessingError || webRequest.result == UnityWebRequest.Result.ProtocolError)
            {
                Debug.Log("Error " + webRequest.error);
            }
            else
            {
                Debug.Log(":\nReceived: " + webRequest.downloadHandler.text);
            }
        }
    }

    IEnumerator GetEnginesRdy(string url)
    {
        using (UnityWebRequest webRequest = UnityWebRequest.Get(url))
        {
            yield return webRequest.SendWebRequest();
            if (webRequest.result == UnityWebRequest.Result.ConnectionError || webRequest.result == UnityWebRequest.Result.DataProcessingError || webRequest.result == UnityWebRequest.Result.ProtocolError)
            {
                Debug.Log("Error " + webRequest.error);
            }
            else
            {
                Debug.Log(":\nReceived: " + webRequest.downloadHandler.text);
                currentStep = "LoadMissile";
            }
        }
    }
}



/*private String GetEnginesRdy()
{
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "no-more-antimatiere"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    return jsonResponse;
}*/