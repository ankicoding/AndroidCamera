# AndroidPlugins
Camera,gallery,alert,attachment  feature incorporated in Android application cordova
To use these plugins in your Android Phonegap Application, follow the below steps:-

a.Firstly you need to declare your custom plugin in config.xml. You can found this file in res > xml folder.

<feature name="YourPluginName">
      <param name="android-package" value="com.Phonegap.YourPluginName" />
</feature>
b.Then call the plugin from javascript

For ex,to show Alerts in my application,I have to use AlertList Plugin as 

My Config.xml is:-
<feature name="AlertListPlugin">
		<param name="android-package" value="com.Phonegap.plugin.alertList.AlertListPlugin" />
</feature>
Here, my package name is- 'com.Phonegap.plugin.alertList'

My Js file is:-
var optionlist = [
            			"Add Photo", // this is the title 
                      	"Take Photo", 
                      	"Choose from Gallery", 
                      	
                              ];

		var NGAlertList= {
				 select : function (success, failure) {
  			 cordova.exec(
//  					 function(listitem){
//  						alert( "You selected "+ optionlist[listitem] );
//  					 },
  					 success,
  					function(error) {
             			alert("Error Occured");
             			},
  					 "AlertListPlugin", //Plugin Name
  					 "alertList",   //Action Name
  					optionlist
  							);
  			    }
  				
		}



