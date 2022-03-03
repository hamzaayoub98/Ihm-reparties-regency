using System;
using System.Collections;
using UnityEngine;
using Newtonsoft.Json;
using UnityEngine.Networking;

public class leverGestion : MonoBehaviour
{
    [SerializeField]
    private Power powerIsBack;

    private String url;
    public static bool isSoundPLayed;
    private const float API_CHECK_MAXTIME = 1.0f; // environ 1 secondes
    private float apiCheckCountdown = API_CHECK_MAXTIME;
    private String currentStep; // current game step between BringBackElectricity - RestartEngine - LoadMissile
    // Audio
    public AudioSource lightsOn;

    // Start is called before the first frame update
    void Start()
    {
        currentStep = apiController.beginningStep;
        url = apiController.url;
        isSoundPLayed = false;

    }

    bool boolean = true;
    // Update is called once per frame
    void Update()
    {
        apiCheckCountdown -= Time.deltaTime;

        if (apiCheckCountdown <= 0)
        {

            if (currentStep == "BringBackElectricity")
            {
                StartCoroutine(GetPowerIsBack(url + "courant/status")); // change currentStep if power is back

                float leverZAngle = GameObject.Find("Lever").GetComponent<Transform>().eulerAngles.z;
                if (250 < leverZAngle && leverZAngle < 310)
                {
                    if (!isSoundPLayed)
                    {
                        lightsOn.Play();
                        isSoundPLayed = true;


                    }

                    if (boolean)
                    {
                        StartCoroutine(PostLeverStatus(url + "actionvr?id=" + "activerLevier"));
                        boolean = false;
                    }
                }
                else
                {

                    StartCoroutine(PostLeverStatus(url + "actionvr?id=" + "desactiverLevier"));
                    boolean = true;
                    lightsOn.Stop();
                    isSoundPLayed = false;
                }
                apiCheckCountdown = API_CHECK_MAXTIME;
            }
            else
            {
                lightsOn.Stop();
            }
        }
        

    }

    IEnumerator PostLeverStatus(string url)
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


    IEnumerator GetPowerIsBack(string url)
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
                powerIsBack = JsonConvert.DeserializeObject<Power>(webRequest.downloadHandler.text);
                if (powerIsBack.restart)
                {
                    currentStep = "RestartEngine";
                }
            }
        }
    }
    class Power
    {
        public bool restart;
        public Power()
        {
            this.restart = false;
        }
    }
}




/*private String PostElectricityNotRdy()
{
  HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "actionvr?id=" + "desactiverLevier"));
  HttpWebResponse response = (HttpWebResponse)request.GetResponse();
  StreamReader reader = new StreamReader(response.GetResponseStream());
  string jsonResponse = reader.ReadToEnd();
  Debug.Log("API - post ElectricityNotRdy");
  return jsonResponse;
}

private String PostElectricityRdy()
{
  HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "actionvr?id=" + "activerLevier"));
  HttpWebResponse response = (HttpWebResponse)request.GetResponse();
  StreamReader reader = new StreamReader(response.GetResponseStream());
  string jsonResponse = reader.ReadToEnd();

  Debug.Log("API - post ElectricityRdy");
  return jsonResponse;
}
private String GetPowerIsBack()
{
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "courant/status"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    Debug.Log("API - get ElectricityIsBack " + jsonResponse);
    return jsonResponse;
}*/