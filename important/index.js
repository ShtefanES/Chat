// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.sendPush = functions.database.ref('/messages/{messageId}').onCreate(event => {
	
	const messageId = event.params.messageId;
	
	if (event.data.val()) {
		console.log('message Id = ', messageId);
	}
  
	const msgPayloadPromise = admin.database().ref(`/messages/${messageId}/payload`).once('value');
	const senderIdPromise = admin.database().ref(`/messages/${messageId}/user_id`).once('value');
	const typeMsgPromise = admin.database().ref(`/messages/${messageId}/type`).once('value');

	return Promise.all([msgPayloadPromise, senderIdPromise, typeMsgPromise]).then(results => {
		const msgPayload = results[0];
		const senderId = results[1];
		const typeMsg = results[2];
  
		const msgPayloadValue = msgPayload.val(); 
		const senderIdValue = senderId.val(); 
		let typeMsgValue = typeMsg.val(); 
  
		console.log('msgPayload Value = ', msgPayloadValue);
		console.log('userId Value = ', senderIdValue);
		console.log('type Value = ', typeMsgValue);
  
		let msg;
  
		if(typeMsgValue == 1)
		{
			console.log('IF typeMsgValue == 1')
			msg = msgPayloadValue;
		}else if(typeMsgValue == 2)
		{
			console.log('IF typeMsgValue == 2')
			msg = "audio message";
		}else if(typeMsgValue == 0){
			return console.log('return typeMsgValue == 0');
		}
  
		const senderNamePromise = admin.database().ref(`/users/${senderIdValue}/name`).once('value');
  
		// Get the list of users promoses.
		const usersPromise = admin.database().ref(`/users`).once('value');
  
		return Promise.all([senderNamePromise, usersPromise]).then(results => {
			const senderName = results[0];
			const users = results[1];
  
			const senderNameValue = senderName.val();
   
			console.log('senderName Value = ', senderNameValue);
   
			// Listing all users.
			const usersValues = Object.keys(users.val());
  
			console.log('users Values = ', usersValues);
  
			let tokenPromises = [];
			for (let usr of usersValues) {
				if(usr != senderIdValue){
					let tokenPromise = admin.database().ref(`/users/${usr}/notification_token`).once('value');
					
					tokenPromises.push(tokenPromise);				
				}
			}
  
			return Promise.all(tokenPromises).then(results => {
				// This registration token comes from the client FCM SDKs.
				let tokensValues = [];
				for (let tkn of results) {
					let tokenValue = tkn.val();
					if(tokenValue != ""){
						tokensValues.push(tokenValue);
					}
				}
	
				for(let i of tokensValues) {
					console.log('i = ', i);	
				}
 
				// Notification details.
				let payload = {
					notification: {
						title: senderNameValue,
						body: msg,
						sound: 'default'
					}
				};
	
				return admin.messaging().sendToDevice(tokensValues, payload).then(function(response) {
						// See the MessagingDevicesResponse reference documentation for
						// the contents of response.
						console.log("Successfully sent message:", response);
					}).catch(function(error) {
						console.log("Error sending message:", error);
				});
			});
		});
	});
});