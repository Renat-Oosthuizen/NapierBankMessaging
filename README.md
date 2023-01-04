# NapierBankMessaging

This is an application that I made while studying at Napier University.

The application receives XML formatted messages, either directly typed or from a file. The messages can be of type Tweet, SMS, Normal Email or SIR Email.
The messages are then be processed so that abbreviations (contained in a textwords.csv file) are expanded in Tweets and SMS, and URLs are removed from both types of emails. The identified URLs are stored in a separate data structure. Hashtags and embedded Twitter IDs inside of Tweets as well as Sort Code and Nature of Incident in SIR Emails are also stored in the system. The processed message is then stored in a JSON file and the text of the processed message is displayed to the user in the GUI. The user has the option to view the collected data from the processed messages in the form of lists. The Trending List displays the number of times that each hashtag was encountered across all processed messages during the session. Mentions List displays all the embedded Twitter IDs encountered across all processed messages during the session. The SIR List displays all the combinations of Sort Code and Nature of Incident contents encountered across all processed messages during the session.

Thanks to this project I gained experience with using Maven, multi-threading and JSON. 

Data Input Screen of the application:
![image](https://user-images.githubusercontent.com/79414856/210655788-d5e5ceba-f447-4cdd-8e74-936e0d5c3c4b.png)

Screen showing the stored data from the session's processed messages:
![image](https://user-images.githubusercontent.com/79414856/210655959-567baeaa-6b9a-4378-bb83-17075914679e.png)

