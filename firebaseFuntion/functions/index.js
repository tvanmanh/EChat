'use strict'

 const functions = require('firebase-functions');
 const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/notification/{user_id}/{notification_id}').onWrite((change, context) => {
const user_id = context.params.user_id
const notification = context.params.notification;

console.log('We have notification to send to : ', user_id);

if(!change.after.val())
{
return console.log('A notification has been deleted from the database : ', notification_id);

}
const deviceToken = admin.database().ref('/users/${user_id}/device_token').once('value');

return deviceToken.then(result =>{
const token_id = result.val();
const payload = {
notification :{
title : "Friend Request",
body : "you have received a new Friend Request",
icon : "default"
}
};
return admin.messaging().sendToDevice(token_id , payload).then(response =>{
return console.log('This was the notification Feature');


});

});

});