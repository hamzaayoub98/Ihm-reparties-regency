using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using Newtonsoft.Json;
using UnityEngine.Networking;

public class apiController : MonoBehaviour
{
    [SerializeField]
    private Power powerIsBack;

    [SerializeField]
    private String electricityLampLeft;

    [SerializeField]
    private String electricityLampRight;

    [SerializeField]
    private Antimatter antimatterInfo;

    [SerializeField]
    private MissileLaunched missileStatus;

    private const float API_CHECK_MAXTIME = 1.0f;
    private float apiCheckCountdown = API_CHECK_MAXTIME;

    private Light ambianceLight;
    private String currentStep; // current game step between BringBackElectricity - RestartEngine - LoadMissile - EndMission1
    public static String beginningStep = "BringBackElectricity"; // global var to have all scripts synchronised
    public static String url = "http://9766-81-185-160-253.eu.ngrok.io/"; //http://42dc-134-59-215-253.eu.ngrok.io/
    public AudioSource pistonAudio, helperPopAudio,missileLaunchedAudio,antimatterPopAudio,explosionAudio,successAudio,engineOnAudio;
    private TextMesh gameStepsText;
    private GameObject missileTube, basketText, antimatterHelper, leverHelper;
    private List<GameObject> asteroids, missiles;
    private float timeToDisplayHelpers;

    // Start is called before the first frame update
    void Start()
    {
        timeToDisplayHelpers = 100.0f;
        missileTube = GameObject.Find("MissileTube");
        currentStep = beginningStep;
        asteroids = new List<GameObject>();
        antimatterHelper = GameObject.Find("HelpAntimatter");
        antimatterHelper.SetActive(false);
        leverHelper = GameObject.Find("HelpLever");
        leverHelper.SetActive(false);
        basketText = GameObject.Find("BasketCounter");
        basketText.SetActive(false);
        gameStepsText = GameObject.Find("GameSteps").GetComponent<TextMesh>();
        gameStepsText.color = new Color(0, 0, 0, 0);

        for (int i =1; i <= 10; i++)
        {
            GameObject asteroid = GameObject.Find("Asteroid_5 (" + i + ")");
            asteroid.SetActive(false);
            asteroids.Add(asteroid);
        }

        missiles = new List<GameObject>();
        for (int i = 1; i <= 3; i++)
        {
            missiles.Add(GameObject.Find("missile (" + i + ")"));
        }

        ambianceLight = GameObject.Find("Directional Light").GetComponent<Light>();

    }


    int previousAntimatterCount = 0;
    int antimatterToAdd = 0;
    int secondeCounter = 60;
    int displayGameStepsSeconds = 60;


    bool isSoundPlayed = false;
    bool repositionMissileTube = false;
    bool displayGameSteps = false;
    bool hideGameSteps = false;

    GameObject missileSelected;

    void Update()
    {
        
        apiCheckCountdown -= Time.deltaTime;
        
        if (apiCheckCountdown <= 0)
        {
            switch (currentStep)
            {
                case "BringBackElectricity":

                    Invoke("DisplayLeverHelper", timeToDisplayHelpers);
                    if (secondeCounter == displayGameStepsSeconds)
                    {
                        DisplayGameSteps();
                        secondeCounter = 0;
                    }
                   
                    StartCoroutine(GetElectricityLampRight(url + "slider1/value")); // update noElectricityLampRight status 
                    if (electricityLampRight != null && electricityLampRight == "true") // if LampRight is on
                    { 
                        if (leverGestion.isSoundPLayed) // if lever is activated
                        { ChangelampsColor(Color.green, true, false);  }
                        else 
                        { ChangelampsColor(Color.red, true, false); } 
                    }
                    else 
                    { ChangelampsColor(Color.white, true, false); }

                    StartCoroutine(GetElectricityLampLeft(url + "slider2/value")); // update noElectricityLampLeft status
                    if (electricityLampLeft != null && electricityLampLeft == "true") // if LampLeft is on
                    { 
                        if (leverGestion.isSoundPLayed) // if lever is activated
                        { ChangelampsColor(Color.green, false, true); } 
                        else 
                        { ChangelampsColor(Color.red, false, true); } }
                    else
                    { ChangelampsColor(Color.white, false, true); }

                    StartCoroutine(GetPowerIsBack(url + "courant/status"));
                    if (powerIsBack != null && powerIsBack.restart) // when electricity is back
                    {
                        successAudio.Play();
                        ChangelampsColor(Color.green, true, true);
                        ambianceLight.intensity = 1;
                        currentStep = "RestartEngine";
                        leverHelper.SetActive(false);
                        hideGameSteps = false;
                        gameStepsText.text = "[Fait] Allumer le courant\n[    ] Redémarrer le moteur\n[    ] Lancer le missile";
                        DisplayGameSteps();
                    }
                    break;
                    
                case "RestartEngine":

                    Invoke("DisplayAntimatterHelper", timeToDisplayHelpers);
                    if (secondeCounter == displayGameStepsSeconds)
                    {
                        DisplayGameSteps();
                        secondeCounter = 0;
                    }
                    basketText.SetActive(true);
                    StartCoroutine(GetAntimatter(url + "antimatiere")); // update antimatterInfo object

                    if (antimatterInfo != null)
                    {
                        antimatterToAdd = antimatterInfo.antimatiereValue - previousAntimatterCount;
                        Debug.Log(antimatterToAdd + " = " + antimatterInfo.antimatiereValue + " -  " + previousAntimatterCount);
                        if (antimatterToAdd > 0)
                        {
                            Debug.Log("popping antimatter");
                            antimatterPopAudio.Play();
                            asteroids[previousAntimatterCount].SetActive(true);//.SetActive(true);
                            previousAntimatterCount++;
                        }
                    }

                    if (basketText.GetComponent<TextMesh>().text == "0")
                    {
                        engineOnAudio.Play();
                        successAudio.Play();
                        currentStep = "LoadMissile";
                        hideGameSteps = false;
                        antimatterHelper.SetActive(false);
                        gameStepsText.text = "[Fait] Allumer le courant\n[Fait] Redémarrer le moteur\n[    ] Lancer le missile";
                        DisplayGameSteps();

                    }

                    break;

                case "LoadMissile":
                    if (secondeCounter == displayGameStepsSeconds)
                    {
                        DisplayGameSteps();
                        secondeCounter = 0;
                    }
                    foreach (GameObject missile in missiles)
                    {
                        // Debug VR
                        /*GameObject.Find("DebugText").GetComponent<TextMesh>().text = (missile.transform.position.x)+ " " + (missile.transform.position.x > -2.9f && missile.transform.position.x < -2.6f)
                        + " \n" + missile.transform.position.y + " " + (missile.transform.position.y > 1.72f && missile.transform.position.y < 1.89f)
                        + " \n" + missile.transform.position.z + " " + (missile.transform.position.z > -0.87f && missile.transform.position.z < -0.68f)
                        + " \n" + missile.transform.eulerAngles.x + " " + (missile.transform.eulerAngles.x > 300f && missile.transform.eulerAngles.x < 303f)
                        + " \n" + missile.transform.eulerAngles.y ;*/
                        if (missile.transform.position.x > -2.9f && missile.transform.position.x < -2.6f &&
                            missile.transform.position.y > 1.72f && missile.transform.position.y < 1.89f &&
                            missile.transform.position.z > -0.87f && missile.transform.position.z < -0.68f)
                        {
                            Destroy(missile.GetComponent("XRGrabInteractable"));
                            Destroy(missile.GetComponent("Rigidbody"));
                            missiles.Remove(missile);
                            repositionMissileTube = true;
                            isSoundPlayed = false;
                            StartCoroutine(GetMissilePlaced(url + "placerMissile"));
                            missileSelected = missile;
                        }
                    }
                    break;
                case "EndMission1":
                    StartCoroutine(GetMissileLaunched(url + "missile/launched"));
                    if (missileStatus != null && missileStatus.missileLaunched)
                    {
                        missileLaunchedAudio.Play();
                        Invoke("ExplosionAudio", 2f);
                        currentStep = "mission2Start";
                    }
                    break;
            }
        apiCheckCountdown = API_CHECK_MAXTIME;
            secondeCounter++;
        }

        if (currentStep == "LoadMissile" && missiles.Count == 3 && missileTube.transform.position.x < -2.967f)
        {    
            missileTube.transform.position = missileTube.transform.position + new Vector3( 0.0015f,0f,0f);
            if (!isSoundPlayed)
            {
                pistonAudio.Play();
                Debug.Log("play");
                isSoundPlayed = true;
            }
        }
        //Debug
        //GameObject.Find("DebugText").GetComponent<TextMesh>().text = missileTube.transform.position.x.ToString();
        if (repositionMissileTube && missileTube.transform.position.x > -3.53f) //Replace MissileTube
        {
            if (!isSoundPlayed)
            {
                pistonAudio.Play();
                isSoundPlayed = true;
            }
            missileTube.transform.position = missileTube.transform.position - new Vector3(0.0015f, 0f, 0f);
            missileSelected.transform.position = missileSelected.transform.position - new Vector3(0.0015f, 0f, 0f);
            currentStep = "EndMission1";
        }
            

        if (displayGameSteps)
        {
            if (gameStepsText.color.a != 1)
            {
                gameStepsText.color = new Color(0, 0, 0, gameStepsText.color.a + 0.01f);
            }
            else
            {
                displayGameSteps = false;
                Invoke("HideGameSteps", 2.0f);
            }
        }
        if (hideGameSteps)
        {
            if (gameStepsText.color.a != 0)
            {
                gameStepsText.color = new Color(0, 0, 0, gameStepsText.color.a - 0.01f);
            }
            else
            {
                hideGameSteps = false;
                Invoke("DisplayGameSteps", 5.0f);
            }
        }
        

        /*if (currentStep == "LoadMissile" && missiles.Count == 2 && missileTube.transform.position.x < -2.967f)
        {
            missileTube.transform.position = missileTube.transform.position + new Vector3(0.001f, 0f, 0f);
            if (!isSoundPlayed)
            {
                pistonAudio.Play();
                isSoundPlayed = true;
            }
        }*/
    }



    void DisplayGameSteps()
    {
        displayGameSteps = true;
    }
    void HideGameSteps()
    {
        hideGameSteps = true;
    }
    private void ChangelampsColor(Color color,bool lampRight, bool lampLeft)
    {
        if (lampRight)
        {
            GameObject.Find("LampRight").GetComponent<Renderer>().material.SetColor("_Color", color);
            GameObject.Find("LampLightRight").GetComponent<Light>().intensity = 1;
            GameObject.Find("LampLightRight").GetComponent<Light>().color = color;
        }
        if (lampLeft)
        {
            GameObject.Find("LampLeft").GetComponent<Renderer>().material.SetColor("_Color", color);
            GameObject.Find("LampLightLeft").GetComponent<Light>().intensity = 1;
            GameObject.Find("LampLightLeft").GetComponent<Light>().color = color;
        }
    }

    IEnumerator GetElectricityLampRight(string url)
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
                if (Int16.Parse(webRequest.downloadHandler.text) == 100)
                {
                    electricityLampRight = "true";
                }
                else
                {
                    electricityLampRight = "false";
                }
            }
        }
    }
    IEnumerator GetElectricityLampLeft(string url)
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
                if (Int16.Parse(webRequest.downloadHandler.text) == 100)
                {
                    electricityLampLeft = "true";
                }
                else
                {
                    electricityLampRight = "false";
                }
            }
        }
    }
    IEnumerator GetMissilePlaced(string url)
    {
        using (UnityWebRequest webRequest = UnityWebRequest.Get(url))
        {
            yield return webRequest.SendWebRequest();
            if (webRequest.result == UnityWebRequest.Result.ConnectionError)
            {
                Debug.Log("Error " + webRequest.error);
            }
            else
            {
                Debug.Log(":\nReceived: " + webRequest.downloadHandler.text);
            }
        }
    }
    IEnumerator GetAntimatter(string url)
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
                antimatterInfo = JsonConvert.DeserializeObject<Antimatter>(webRequest.downloadHandler.text);
            }
        }
    }
IEnumerator GetMissileLaunched(string url)
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
                missileStatus = JsonConvert.DeserializeObject<MissileLaunched>(webRequest.downloadHandler.text);
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
            }
        }
    }
    bool AntimatterHelperSound = false;
    void DisplayAntimatterHelper()
    {
        if(currentStep == "RestartEngine") 
        { 
            antimatterHelper.SetActive(true);
            if (!AntimatterHelperSound)
            {
                helperPopAudio.Play();
                Invoke("stopHelperPopAudio", 1f);
                AntimatterHelperSound = true;
            }
        }
        
    }
    bool leverHelperSound = false;
    void DisplayLeverHelper()
    {
        if (currentStep == "BringBackElectricity")
        {
            leverHelper.SetActive(true);
            Debug.Log(leverHelperSound);
            if (!leverHelperSound)
            {
                helperPopAudio.Play();
                Invoke("stopHelperPopAudio", 1f);
                leverHelperSound = true;
            }
        }
    }
    void ExplosionAudio()
    {
        explosionAudio.Play();
        Invoke("SuccessAudio", 3f);
    }
    void SuccessAudio()
    {
        hideGameSteps = false;
        gameStepsText.text = "[Fait] Allumer le courant\n[Fait] Redémarrer le moteur\n[Fait] Lancer le missile";
        DisplayGameSteps();
        successAudio.Play();
    }
    void stopHelperPopAudio()
    {
        Debug.Log("là");
        helperPopAudio.Stop();
    }
    class MissileLaunched
    {
        public bool missileLaunched;
        public MissileLaunched()
        {
            this.missileLaunched = false;
        }
    }
    class Antimatter
    {
        public int antimatiereValue;
        public Antimatter()
        {
            this.antimatiereValue = 0;
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

/*
private String GetPowerIsBack()
{
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "courant/status"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    Debug.Log("API - get ElectricityIsBack " + jsonResponse);
    return jsonResponse;
}
private String GetMissilePlaced()
{

    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "placerMissile"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    Debug.Log("API - get MissilePlaced " + jsonResponse);
    return jsonResponse;
}
private bool GetElectricityLampLeft()
{   
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "slider2/value"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    Debug.Log("API - get RightSlider value " + jsonResponse);
    if (Int16.Parse(jsonResponse) == 100)
    {
        return true;
    }
    return false;
}

private bool GetElectricityLampRight()
{
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "slider1/value"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    Debug.Log("API - get RightSlider value " + jsonResponse);
    if (Int16.Parse(jsonResponse) == 100)
    {
        return true;
    }
    return false;
}

private String PostAction(int v)
{
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "actionvr?id=" + v));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    return jsonResponse;
}

private String GetAntimatter()
{
    Debug.Log("API - get Antimatter");
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(url + "antimatiere"));
    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
    StreamReader reader = new StreamReader(response.GetResponseStream());
    string jsonResponse = reader.ReadToEnd();
    return jsonResponse;
}
*/