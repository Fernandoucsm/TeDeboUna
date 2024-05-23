
// The Cloud Functions for Firebase SDK to create Cloud Functions and triggers.
const {logger} = require("firebase-functions");
const {onRequest} = require("firebase-functions/v2/https");
const {onDocumentCreated} = require("firebase-functions/v2/firestore");

// The Firebase Admin SDK to access Firestore.
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");

initializeApp();
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.deleteUserPostsAndComments = functions.firestore
    .document('users/{userId}')
    .onDelete((snap, context) => {
        const userId = context.params.userId;
        const postsRef = admin.firestore().collection('posts');

        return postsRef.where('userId', '==', userId).get()
            .then(querySnapshot => {
                // Crear un lote para eliminar las publicaciones y comentarios
                let batch = admin.firestore().batch();

                // Crear un array para almacenar todas las promesas de los comentarios
                let promises = [];

                querySnapshot.forEach(doc => {
                    // Para cada publicación, obtener los comentarios y agregarlos al lote de eliminación
                    let promise = doc.ref.collection('comments').get().then(commentSnapshot => {
                        commentSnapshot.forEach(commentDoc => {
                            batch.delete(commentDoc.ref);
                        });
                    });

                    promises.push(promise);

                    // Agregar la publicación al lote de eliminación
                    batch.delete(doc.ref);
                });

                // Esperar a que todas las promesas de los comentarios se resuelvan antes de comprometer el lote
                return Promise.all(promises).then(() => batch.commit());
            })
            .catch(error => {
                console.log('Error al obtener y eliminar las publicaciones y comentarios: ', error);
            });
    });