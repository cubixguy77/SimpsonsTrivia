package com.triviabilities.network;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.triviabilities.interfaces.QuestionFetcherListener;
import com.triviabilities.models.Question;

import org.w3c.dom.Document;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class QuestionFetcher extends AsyncTask<Integer, Question, Boolean>
{
    private InputStream inputStream;
    private QuestionFetcherListener listener;
    private boolean bonusRound;
    private int[] questionIDs;
    private boolean isPaused = false;

    public QuestionFetcher(String xmlFileName, Context context, QuestionFetcherListener listener, boolean bonusRound, int[] questionIDs)
    {
        this.listener = listener;
        this.bonusRound = bonusRound;
        this.questionIDs = questionIDs;

        Resources resources = context.getResources();
        AssetManager assetManager = resources.getAssets();
        try
        {
            this.inputStream = assetManager.open(xmlFileName);
        }
        catch(Exception e) {

        }
    }

    public void pause()
    {
        isPaused = true;
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    public void resume()
    {
        isPaused = false;
    }

    @Override
    protected Boolean doInBackground(Integer... index)
    {
        return GetQuestions();
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        this.listener = null;
        this.inputStream = null;
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Question... values) {
        if (this.bonusRound)
            listener.onBonusQuestionReturned(values[0]);
        else
            listener.onQuestionReturned(values[0]);

    }

    public Boolean GetQuestions()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            XPath xpath = XPathFactory.newInstance().newXPath();

            for (int i=0; i<questionIDs.length; i++)
            {
                while(isPaused())
                {
                    if (isCancelled())
                        return false;

                    sleep(5000);
                }

                String index = Integer.toString(questionIDs[i]);
                XPathExpression expr = xpath.compile("/Questions/Question[@QuestionNumber=\"" + index + "\"]/QuestionText/text()");
                String questionText = (String) expr.evaluate(doc, XPathConstants.STRING);

                expr = xpath.compile("/Questions/Question[@QuestionNumber=\"" + index + "\"]/Answers/Answer[1]/text()");
                String answerA = (String) expr.evaluate(doc, XPathConstants.STRING);
                expr = xpath.compile("/Questions/Question[@QuestionNumber=\"" + index + "\"]/Answers/Answer[2]/text()");
                String answerB = (String) expr.evaluate(doc, XPathConstants.STRING);
                expr = xpath.compile("/Questions/Question[@QuestionNumber=\"" + index + "\"]/Answers/Answer[3]/text()");
                String answerC = (String) expr.evaluate(doc, XPathConstants.STRING);
                expr = xpath.compile("/Questions/Question[@QuestionNumber=\"" + index + "\"]/Answers/Answer[4]/text()");
                String answerD = (String) expr.evaluate(doc, XPathConstants.STRING);

                publishProgress(new Question(questionText, answerA, answerB, answerC, answerD, "A"));
            }
        }
        catch (Exception p){
            p.printStackTrace();
        }

        return true;
    }

    private void sleep(long sleepDuration)
    {
        try
        {
            if (!isCancelled())
                Thread.sleep(sleepDuration);
        }
        catch(InterruptedException e)
        {
            listener = null;
            inputStream = null;
        }
    }


    /*
    private Question GetQuestionByNode2(NodeList nodeList)
    {
        Node questionTextNode = nodeList.item(1);
        String questionText = questionTextNode.getNodeValue();

        Node answerNodes = nodeList.item(3);
        Node answerNodeA = answerNodes.getFirstChild();
        Node answerNodeB = answerNodeA.getNextSibling();
        Node answerNodeC = answerNodeB.getNextSibling();
        Node answerNodeD = answerNodeC.getNextSibling();

        String answerA = answerNodeA.getNodeValue();
        String answerB = answerNodeB.getNodeValue();
        String answerC = answerNodeC.getNodeValue();
        String answerD = answerNodeD.getNodeValue();

        return new Question(questionText, answerA, answerB, answerC, answerD, "A");
    }




    private Question GetQuestionByNode(Element node)
    {
        Node questionTextNode = (Node) node.getElementsByTagName("QuestionText");
        String questionText = questionTextNode.getNodeValue();

        Node answerNodes = (Node) node.getElementsByTagName("Answers");
        Node answerNodeA = answerNodes.getFirstChild();
        Node answerNodeB = answerNodeA.getNextSibling();
        Node answerNodeC = answerNodeA.getNextSibling();
        Node answerNodeD = answerNodeA.getNextSibling();

        String answerA = answerNodeA.getNodeValue();
        String answerB = answerNodeB.getNodeValue();
        String answerC = answerNodeC.getNodeValue();
        String answerD = answerNodeD.getNodeValue();

        return new Question(questionText, answerA, answerB, answerC, answerD, "A");
    }
    */
}
