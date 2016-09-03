package brains.mock.firebasetest.rxfirebase;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import brains.mock.firebasetest.rxfirebase.exceptions.RxFirebaseDataCastException;
import brains.mock.firebasetest.rxfirebase.exceptions.RxFirebaseDataException;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class RxFirebase {

    public static class Auth {

        @NonNull
        public static Observable<AuthResult> signInAnonymously(@NonNull final FirebaseAuth firebaseAuth) {
            return Observable.create(new Observable.OnSubscribe<AuthResult>() {
                @Override
                public void call(final Subscriber<? super AuthResult> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.signInAnonymously());
                }
            });
        }

        @NonNull
        public static Observable<AuthResult> signInWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                        @NonNull final String email,
                                                                        @NonNull final String password) {
            return Observable.create(new Observable.OnSubscribe<AuthResult>() {
                @Override
                public void call(final Subscriber<? super AuthResult> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.signInWithEmailAndPassword(email, password));
                }
            });
        }

        @NonNull
        public static Observable<AuthResult> signInWithCredential(@NonNull final FirebaseAuth firebaseAuth,
                                                                  @NonNull final AuthCredential credential) {
            return Observable.create(new Observable.OnSubscribe<AuthResult>() {
                @Override
                public void call(final Subscriber<? super AuthResult> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.signInWithCredential(credential));
                }
            });
        }

        @NonNull
        public static Observable<AuthResult> signInWithCustomToken(@NonNull final FirebaseAuth firebaseAuth,
                                                                   @NonNull final String token) {
            return Observable.create(new Observable.OnSubscribe<AuthResult>() {
                @Override
                public void call(final Subscriber<? super AuthResult> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.signInWithCustomToken(token));
                }
            });
        }

        @NonNull
        public static Observable<AuthResult> createUserWithEmailAndPassword(@NonNull final FirebaseAuth firebaseAuth,
                                                                            @NonNull final String email,
                                                                            @NonNull final String password) {
            return Observable.create(new Observable.OnSubscribe<AuthResult>() {
                @Override
                public void call(final Subscriber<? super AuthResult> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.createUserWithEmailAndPassword(email, password));
                }
            });
        }

        @NonNull
        public static Observable<ProviderQueryResult> fetchProvidersForEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                                             @NonNull final String email) {
            return Observable.create(new Observable.OnSubscribe<ProviderQueryResult>() {
                @Override
                public void call(final Subscriber<? super ProviderQueryResult> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.fetchProvidersForEmail(email));
                }
            });
        }

        @NonNull
        public static Observable<Void> sendPasswordResetEmail(@NonNull final FirebaseAuth firebaseAuth,
                                                              @NonNull final String email) {
            return Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(final Subscriber<? super Void> subscriber) {
                    RxHandler.assignOnTask(subscriber, firebaseAuth.sendPasswordResetEmail(email));
                }
            });
        }

        @NonNull
        public static Observable<FirebaseUser> observeAuthState(@NonNull final FirebaseAuth firebaseAuth) {
            return Observable.create(new Observable.OnSubscribe<FirebaseUser>() {
                @Override
                public void call(final Subscriber<? super FirebaseUser> subscriber) {
                    final FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(firebaseAuth.getCurrentUser());
                            }
                        }
                    };
                    firebaseAuth.addAuthStateListener(authStateListener);

                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            firebaseAuth.removeAuthStateListener(authStateListener);
                        }
                    }));
                }
            });
        }
    }

    public static class Database {

        @NonNull
        public static <T> Observable<T> observeSingleValue(@NonNull final Query query, @NonNull final Class<T> clazz) {
            return Observable.create(new Observable.OnSubscribe<T>() {
                @Override
                public void call(final Subscriber<? super T> subscriber) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            T value = dataSnapshot.getValue(clazz);
                            if (value != null) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(value);
                                }
                            } else {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onError(new RxFirebaseDataCastException("unable to cast firebase data response to " + clazz.getSimpleName()));
                                }
                            }

                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(new RxFirebaseDataException(error));
                            }
                        }
                    });

                }
            });
        }

        @NonNull
        public static <T> Observable<T> observeValues(@NonNull final Query query, @NonNull final Class<T> clazz) {
            return Observable.create(new Observable.OnSubscribe<T>() {
                @Override
                public void call(final Subscriber<? super T> subscriber) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                T value = childSnapshot.getValue(clazz);
                                if (value == null) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onError(new RxFirebaseDataCastException("unable to cast firebase data response to " + clazz.getSimpleName()));
                                    }
                                } else {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(value);
                                    }
                                }
                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(new RxFirebaseDataException(error));
                            }
                        }
                    });
                }

            });
        }

        @NonNull
        public static <T> Observable<List<T>> observeValuesList(@NonNull final Query query, @NonNull final Class<T> clazz) {
            return Observable.create(new Observable.OnSubscribe<List<T>>() {
                @Override
                public void call(final Subscriber<? super List<T>> subscriber) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<T> items = new ArrayList<T>();
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                T value = childSnapshot.getValue(clazz);
                                if (value == null) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onError(new RxFirebaseDataCastException("unable to cast firebase data response to " + clazz.getSimpleName()));
                                    }
                                } else {
                                    items.add(value);
                                }
                            }

                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(items);
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(new RxFirebaseDataException(error));
                            }
                        }
                    });
                }

            });
        }

        @NonNull
        public static <T> Observable<Map<String, T>> observeValuesMap(@NonNull final Query query, @NonNull final Class<T> clazz) {
            return Observable.create(new Observable.OnSubscribe<Map<String, T>>() {
                @Override
                public void call(final Subscriber<? super Map<String, T>> subscriber) {
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, T> items = new HashMap<String, T>();
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                T value = childSnapshot.getValue(clazz);
                                if (value == null) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onError(new RxFirebaseDataCastException("unable to cast firebase data response to " + clazz.getSimpleName()));
                                    }
                                } else {
                                    items.put(childSnapshot.getKey(), value);
                                }
                            }

                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(items);
                                subscriber.onCompleted();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onError(new RxFirebaseDataException(error));
                            }
                        }
                    });
                }

            });
        }

        @NonNull
        public static <T> Observable<RxFirebaseChildEvent<T>> observeChildrenEvents(@NonNull final Query ref, @NonNull final Class<T> clazz) {
            return Observable.create(new Observable.OnSubscribe<RxFirebaseChildEvent<T>>() {
                @Override
                public void call(final Subscriber<? super RxFirebaseChildEvent<T>> subscriber) {
                    final ChildEventListener childEventListener =
                            ref.addChildEventListener(new ChildEventListener() {

                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(
                                                new RxFirebaseChildEvent<T>(dataSnapshot.getValue(clazz),
                                                        previousChildName,
                                                        RxFirebaseChildEvent.EventType.ADDED));
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(
                                                new RxFirebaseChildEvent<T>(dataSnapshot.getValue(clazz),
                                                        previousChildName,
                                                        RxFirebaseChildEvent.EventType.CHANGED));
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(
                                                new RxFirebaseChildEvent<T>(dataSnapshot.getValue(clazz),
                                                        RxFirebaseChildEvent.EventType.REMOVED));
                                    }
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onNext(
                                                new RxFirebaseChildEvent<T>(dataSnapshot.getValue(clazz),
                                                        previousChildName,
                                                        RxFirebaseChildEvent.EventType.MOVED));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    if (!subscriber.isUnsubscribed()) {
                                        subscriber.onError(new RxFirebaseDataException(error));
                                    }
                                }
                            });

                    subscriber.add(Subscriptions.create(new Action0() {
                        @Override
                        public void call() {
                            ref.removeEventListener(childEventListener);
                        }
                    }));
                }
            });
        }
    }

    public static class Storage {

        @NonNull
        public static Observable<byte[]> getBytes(@NonNull final StorageReference storageRef,
                                                  final long maxDownloadSizeBytes) {
            return Observable.create(new Observable.OnSubscribe<byte[]>() {
                @Override
                public void call(final Subscriber<? super byte[]> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.getBytes(maxDownloadSizeBytes));
                }
            });
        }

        @NonNull
        public static Observable<Uri> getDownloadUrl(@NonNull final StorageReference storageRef) {
            return Observable.create(new Observable.OnSubscribe<Uri>() {
                @Override
                public void call(final Subscriber<? super Uri> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.getDownloadUrl());
                }
            });
        }

        @NonNull
        public static Observable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                        @NonNull final File destinationFile) {
            return Observable.create(new Observable.OnSubscribe<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super FileDownloadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTaskNotCompleted(subscriber, storageRef.getFile(destinationFile));
                }
            });
        }

        @NonNull
        public static Observable<FileDownloadTask.TaskSnapshot> getFile(@NonNull final StorageReference storageRef,
                                                                        @NonNull final Uri destinationUri) {
            return Observable.create(new Observable.OnSubscribe<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super FileDownloadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTaskNotCompleted(subscriber, storageRef.getFile(destinationUri));
                }
            });
        }

        @NonNull
        public static Observable<StorageMetadata> getMetadata(@NonNull final StorageReference storageRef) {
            return Observable.create(new Observable.OnSubscribe<StorageMetadata>() {
                @Override
                public void call(final Subscriber<? super StorageMetadata> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.getMetadata());
                }
            });
        }

        @NonNull
        public static Observable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef) {
            return Observable.create(new Observable.OnSubscribe<StreamDownloadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super StreamDownloadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.getStream());
                }
            });
        }

        @NonNull
        public static Observable<StreamDownloadTask.TaskSnapshot> getStream(@NonNull final StorageReference storageRef,
                                                                            @NonNull final StreamDownloadTask.StreamProcessor processor) {
            return Observable.create(new Observable.OnSubscribe<StreamDownloadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super StreamDownloadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.getStream(processor));
                }
            });
        }


        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                                   @NonNull final byte[] bytes) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putBytes(bytes));
                }
            });
        }

        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putBytes(@NonNull final StorageReference storageRef,
                                                                   @NonNull final byte[] bytes,
                                                                   @NonNull final StorageMetadata metadata) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putBytes(bytes, metadata));
                }
            });
        }

        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                                  @NonNull final Uri uri) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putFile(uri));
                }
            });
        }

        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                                  @NonNull final Uri uri,
                                                                  @NonNull final StorageMetadata metadata) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putFile(uri, metadata));
                }
            });
        }

        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putFile(@NonNull final StorageReference storageRef,
                                                                  @NonNull final Uri uri,
                                                                  @NonNull final StorageMetadata metadata,
                                                                  @NonNull final Uri existingUploadUri) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putFile(uri, metadata, existingUploadUri));
                }
            });
        }

        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                                    @NonNull final InputStream stream,
                                                                    @NonNull final StorageMetadata metadata) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putStream(stream, metadata));
                }
            });
        }

        @NonNull
        public static Observable<UploadTask.TaskSnapshot> putStream(@NonNull final StorageReference storageRef,
                                                                    @NonNull final InputStream stream) {
            return Observable.create(new Observable.OnSubscribe<UploadTask.TaskSnapshot>() {
                @Override
                public void call(final Subscriber<? super UploadTask.TaskSnapshot> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.putStream(stream));
                }
            });
        }

        @NonNull
        public static Observable<StorageMetadata> updateMetadata(@NonNull final StorageReference storageRef,
                                                                 @NonNull final StorageMetadata metadata) {
            return Observable.create(new Observable.OnSubscribe<StorageMetadata>() {
                @Override
                public void call(final Subscriber<? super StorageMetadata> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.updateMetadata(metadata));
                }
            });
        }

        @NonNull
        public static Observable<Void> delete(@NonNull final StorageReference storageRef) {
            return Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(final Subscriber<? super Void> subscriber) {
                    RxHandler.assignOnTask(subscriber, storageRef.delete());
                }
            });
        }
    }
}
