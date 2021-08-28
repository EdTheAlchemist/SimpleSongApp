Lesson notes for project:
1. The Service class is the generic service and is the most flexible in use. Though many of the actions can
   be accomplished using IntentService, regular Services are more flexible. Service is the parent of the
   IntentService class.
   - The onStartCommand function is called when a service is started
   * https://developer.android.com/guide/components/services
2. For Music files, create a RAW resource folder and put the file in there.
   - Be sure to name the songs without any special character aside from underscore
3. There are many pre-sets that can already do many of the things commonly done in Android. There are also
   other ways to implement tasks that initially look like it can be done using Services. Look at:
   - Chronometer
       https://developer.android.com/reference/android/widget/Chronometer
   - CountDownTimer
       https://developer.android.com/reference/android/os/CountDownTimer
   - AsyncTask vs runOnUiThread
   * When looking at a task in the industry, look at existing libraries as Android anticipates commonly
     used actions and typically creates libraries to make development quicker.

Copyright
   The song BETTER DAYS is a Royalty Free Music piece taken from Bensound

Self-Practise:
   As a practise, modify the project so that the Stop and Play buttons work. The Stop button will stop
   the song being played while the Play button will play the track once more.
