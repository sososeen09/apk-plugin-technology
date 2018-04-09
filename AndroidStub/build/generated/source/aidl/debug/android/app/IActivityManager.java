/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /works/StudioWorkSpace/Personal/apk-plugin-technology/AndroidStub/src/main/aidl/android/app/IActivityManager.aidl
 */
package android.app;
/**
 * @author johnsonlee
 */
public interface IActivityManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.app.IActivityManager
{
private static final java.lang.String DESCRIPTOR = "android.app.IActivityManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an android.app.IActivityManager interface,
 * generating a proxy if needed.
 */
public static android.app.IActivityManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.app.IActivityManager))) {
return ((android.app.IActivityManager)iin);
}
return new android.app.IActivityManager.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startService:
{
data.enforceInterface(DESCRIPTOR);
android.app.IApplicationThread _arg0;
_arg0 = android.app.IApplicationThread.Stub.asInterface(data.readStrongBinder());
android.content.Intent _arg1;
if ((0!=data.readInt())) {
_arg1 = android.content.Intent.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
int _arg4;
_arg4 = data.readInt();
android.content.ComponentName _result = this.startService(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_stopService:
{
data.enforceInterface(DESCRIPTOR);
android.app.IApplicationThread _arg0;
_arg0 = android.app.IApplicationThread.Stub.asInterface(data.readStrongBinder());
android.content.Intent _arg1;
if ((0!=data.readInt())) {
_arg1 = android.content.Intent.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
int _arg3;
_arg3 = data.readInt();
int _result = this.stopService(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopServiceToken:
{
data.enforceInterface(DESCRIPTOR);
android.content.ComponentName _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.ComponentName.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.IBinder _arg1;
_arg1 = data.readStrongBinder();
int _arg2;
_arg2 = data.readInt();
boolean _result = this.stopServiceToken(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setServiceForeground:
{
data.enforceInterface(DESCRIPTOR);
android.content.ComponentName _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.ComponentName.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.IBinder _arg1;
_arg1 = data.readStrongBinder();
int _arg2;
_arg2 = data.readInt();
android.app.Notification _arg3;
if ((0!=data.readInt())) {
_arg3 = android.app.Notification.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
boolean _arg4;
_arg4 = (0!=data.readInt());
this.setServiceForeground(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_bindService:
{
data.enforceInterface(DESCRIPTOR);
android.app.IApplicationThread _arg0;
_arg0 = android.app.IApplicationThread.Stub.asInterface(data.readStrongBinder());
android.os.IBinder _arg1;
_arg1 = data.readStrongBinder();
android.content.Intent _arg2;
if ((0!=data.readInt())) {
_arg2 = android.content.Intent.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
java.lang.String _arg3;
_arg3 = data.readString();
android.app.IServiceConnection _arg4;
_arg4 = android.app.IServiceConnection.Stub.asInterface(data.readStrongBinder());
int _arg5;
_arg5 = data.readInt();
java.lang.String _arg6;
_arg6 = data.readString();
int _arg7;
_arg7 = data.readInt();
int _result = this.bindService(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_unbindService:
{
data.enforceInterface(DESCRIPTOR);
android.app.IServiceConnection _arg0;
_arg0 = android.app.IServiceConnection.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unbindService(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_publishService:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _arg0;
_arg0 = data.readStrongBinder();
android.content.Intent _arg1;
if ((0!=data.readInt())) {
_arg1 = android.content.Intent.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
android.os.IBinder _arg2;
_arg2 = data.readStrongBinder();
this.publishService(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_unbindFinished:
{
data.enforceInterface(DESCRIPTOR);
android.os.IBinder _arg0;
_arg0 = data.readStrongBinder();
android.content.Intent _arg1;
if ((0!=data.readInt())) {
_arg1 = android.content.Intent.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
boolean _arg2;
_arg2 = (0!=data.readInt());
this.unbindFinished(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_getIntentSender:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
android.os.IBinder _arg2;
_arg2 = data.readStrongBinder();
java.lang.String _arg3;
_arg3 = data.readString();
int _arg4;
_arg4 = data.readInt();
android.content.Intent[] _arg5;
_arg5 = data.createTypedArray(android.content.Intent.CREATOR);
java.lang.String[] _arg6;
_arg6 = data.createStringArray();
int _arg7;
_arg7 = data.readInt();
android.os.Bundle _arg8;
if ((0!=data.readInt())) {
_arg8 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg8 = null;
}
int _arg9;
_arg9 = data.readInt();
android.content.IIntentSender _result = this.getIntentSender(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9);
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_cancelIntentSender:
{
data.enforceInterface(DESCRIPTOR);
android.content.IIntentSender _arg0;
_arg0 = android.content.IIntentSender.Stub.asInterface(data.readStrongBinder());
this.cancelIntentSender(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPackageForIntentSender:
{
data.enforceInterface(DESCRIPTOR);
android.content.IIntentSender _arg0;
_arg0 = android.content.IIntentSender.Stub.asInterface(data.readStrongBinder());
java.lang.String _result = this.getPackageForIntentSender(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getUidForIntentSender:
{
data.enforceInterface(DESCRIPTOR);
android.content.IIntentSender _arg0;
_arg0 = android.content.IIntentSender.Stub.asInterface(data.readStrongBinder());
int _result = this.getUidForIntentSender(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.app.IActivityManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public android.content.ComponentName startService(android.app.IApplicationThread caller, android.content.Intent service, java.lang.String resolvedType, java.lang.String callingPackage, int userId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.ComponentName _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((caller!=null))?(caller.asBinder()):(null)));
if ((service!=null)) {
_data.writeInt(1);
service.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(resolvedType);
_data.writeString(callingPackage);
_data.writeInt(userId);
mRemote.transact(Stub.TRANSACTION_startService, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.content.ComponentName.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int stopService(android.app.IApplicationThread caller, android.content.Intent service, java.lang.String resolvedType, int userId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((caller!=null))?(caller.asBinder()):(null)));
if ((service!=null)) {
_data.writeInt(1);
service.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(resolvedType);
_data.writeInt(userId);
mRemote.transact(Stub.TRANSACTION_stopService, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean stopServiceToken(android.content.ComponentName className, android.os.IBinder token, int startId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((className!=null)) {
_data.writeInt(1);
className.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder(token);
_data.writeInt(startId);
mRemote.transact(Stub.TRANSACTION_stopServiceToken, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setServiceForeground(android.content.ComponentName className, android.os.IBinder token, int id, android.app.Notification notification, boolean keepNotification) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((className!=null)) {
_data.writeInt(1);
className.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder(token);
_data.writeInt(id);
if ((notification!=null)) {
_data.writeInt(1);
notification.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((keepNotification)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setServiceForeground, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int bindService(android.app.IApplicationThread caller, android.os.IBinder token, android.content.Intent service, java.lang.String resolvedType, android.app.IServiceConnection connection, int flags, java.lang.String callingPackage, int userId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((caller!=null))?(caller.asBinder()):(null)));
_data.writeStrongBinder(token);
if ((service!=null)) {
_data.writeInt(1);
service.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(resolvedType);
_data.writeStrongBinder((((connection!=null))?(connection.asBinder()):(null)));
_data.writeInt(flags);
_data.writeString(callingPackage);
_data.writeInt(userId);
mRemote.transact(Stub.TRANSACTION_bindService, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean unbindService(android.app.IServiceConnection connection) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((connection!=null))?(connection.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unbindService, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void publishService(android.os.IBinder token, android.content.Intent intent, android.os.IBinder service) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder(token);
if ((intent!=null)) {
_data.writeInt(1);
intent.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeStrongBinder(service);
mRemote.transact(Stub.TRANSACTION_publishService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unbindFinished(android.os.IBinder token, android.content.Intent service, boolean doRebind) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder(token);
if ((service!=null)) {
_data.writeInt(1);
service.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(((doRebind)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_unbindFinished, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public android.content.IIntentSender getIntentSender(int type, java.lang.String packageName, android.os.IBinder token, java.lang.String resultWho, int requestCode, android.content.Intent[] intents, java.lang.String[] resolvedTypes, int flags, android.os.Bundle options, int userId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.IIntentSender _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(type);
_data.writeString(packageName);
_data.writeStrongBinder(token);
_data.writeString(resultWho);
_data.writeInt(requestCode);
_data.writeTypedArray(intents, 0);
_data.writeStringArray(resolvedTypes);
_data.writeInt(flags);
if ((options!=null)) {
_data.writeInt(1);
options.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(userId);
mRemote.transact(Stub.TRANSACTION_getIntentSender, _data, _reply, 0);
_reply.readException();
_result = android.content.IIntentSender.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void cancelIntentSender(android.content.IIntentSender sender) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((sender!=null))?(sender.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_cancelIntentSender, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getPackageForIntentSender(android.content.IIntentSender sender) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((sender!=null))?(sender.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getPackageForIntentSender, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int getUidForIntentSender(android.content.IIntentSender sender) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((sender!=null))?(sender.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_getUidForIntentSender, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_startService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stopService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopServiceToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setServiceForeground = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_bindService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unbindService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_publishService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_unbindFinished = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getIntentSender = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_cancelIntentSender = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getPackageForIntentSender = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getUidForIntentSender = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
}
public android.content.ComponentName startService(android.app.IApplicationThread caller, android.content.Intent service, java.lang.String resolvedType, java.lang.String callingPackage, int userId) throws android.os.RemoteException;
public int stopService(android.app.IApplicationThread caller, android.content.Intent service, java.lang.String resolvedType, int userId) throws android.os.RemoteException;
public boolean stopServiceToken(android.content.ComponentName className, android.os.IBinder token, int startId) throws android.os.RemoteException;
public void setServiceForeground(android.content.ComponentName className, android.os.IBinder token, int id, android.app.Notification notification, boolean keepNotification) throws android.os.RemoteException;
public int bindService(android.app.IApplicationThread caller, android.os.IBinder token, android.content.Intent service, java.lang.String resolvedType, android.app.IServiceConnection connection, int flags, java.lang.String callingPackage, int userId) throws android.os.RemoteException;
public boolean unbindService(android.app.IServiceConnection connection) throws android.os.RemoteException;
public void publishService(android.os.IBinder token, android.content.Intent intent, android.os.IBinder service) throws android.os.RemoteException;
public void unbindFinished(android.os.IBinder token, android.content.Intent service, boolean doRebind) throws android.os.RemoteException;
public android.content.IIntentSender getIntentSender(int type, java.lang.String packageName, android.os.IBinder token, java.lang.String resultWho, int requestCode, android.content.Intent[] intents, java.lang.String[] resolvedTypes, int flags, android.os.Bundle options, int userId) throws android.os.RemoteException;
public void cancelIntentSender(android.content.IIntentSender sender) throws android.os.RemoteException;
public java.lang.String getPackageForIntentSender(android.content.IIntentSender sender) throws android.os.RemoteException;
public int getUidForIntentSender(android.content.IIntentSender sender) throws android.os.RemoteException;
}
