/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /works/StudioWorkSpace/plugin-proj/VirtualAPK/AndroidStub/src/main/aidl/android/app/IApplicationThread.aidl
 */
package android.app;
public interface IApplicationThread extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements android.app.IApplicationThread
{
private static final java.lang.String DESCRIPTOR = "android.app.IApplicationThread";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an android.app.IApplicationThread interface,
 * generating a proxy if needed.
 */
public static android.app.IApplicationThread asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof android.app.IApplicationThread))) {
return ((android.app.IApplicationThread)iin);
}
return new android.app.IApplicationThread.Stub.Proxy(obj);
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
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements android.app.IApplicationThread
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
}
}
}
