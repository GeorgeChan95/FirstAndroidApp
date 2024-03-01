package com.george.chapter07_client;

import static android.provider.ContactsContract.Directory.ACCOUNT_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import com.george.chapter07_client.entity.Contact;
import com.george.chapter07_client.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class ContactAddActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "GeorgeTag";

    public String[] PERMISSIONS_CONTACTS = new String[] {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    public String[] PERMISSIONS_SMS = new String[] {
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    public final int REQUEST_CODE_CONTACTS = 1;
    public final int REQUEST_CODE_SMS = 2;

    private EditText et_contact_name;
    private EditText et_contact_phone;
    private EditText et_contact_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        // 校验权限
        PermissionUtil.checkPermission(this, PERMISSIONS_CONTACTS, REQUEST_CODE_CONTACTS);

        et_contact_name = findViewById(R.id.et_contact_name);
        et_contact_phone = findViewById(R.id.et_contact_phone);
        et_contact_email = findViewById(R.id.et_contact_email);

        findViewById(R.id.btn_add_contact).setOnClickListener(this);
        findViewById(R.id.btn_read_contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_add_contact:
                // 创建一个联系人对象
                Contact contact = new Contact();
                contact.name = et_contact_name.getText().toString().trim();
                contact.phone = et_contact_phone.getText().toString().trim();
                contact.email = et_contact_email.getText().toString().trim();

                // 方式一，使用ContentResolver多次写入，每次一个字段
//                addContacts(getContentResolver(), contact);

                // 方式二，批处理方式
                // 每一次操作都是一个 ContentProviderOperation，构建一个操作集合，然后一次性执行
                // 好处是，要么全部成功，要么全部失败，保证了事务的一致性
                addFullContacts(getContentResolver(), contact);
                break;
        }

    }

    /**
     * 批量保存，往手机通讯录一次性添加一个联系人信息（包括主记录、姓名、电话号码、电子邮箱）
     * 使用ContentProviderOperation 批量操作ContentProvider
     * @param contentResolver
     * @param contact
     */
    private void addFullContacts(ContentResolver contentResolver, Contact contact) {
        // 联系人主记录内容操作器
        ContentProviderOperation operation = ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build();

        // 联系人姓名内容操作器
        ContentProviderOperation name = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                // 将第0个操作的id，即 raw_contacts 的 id 作为 data 表中的 raw_contact_id
                .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.Contacts.Data.DATA2, contact.name)
                .build();

        // 联系人电话号码内容操作器
        ContentProviderOperation phone = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                // 将第0个操作的id，即 raw_contacts 的 id 作为 data 表中的 raw_contact_id
                .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.Contacts.Data.DATA1, contact.phone)
                .withValue(ContactsContract.Contacts.Data.DATA2, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .build();

        // 联系人邮箱内容操作器
        ContentProviderOperation email = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                // 将第0个操作的id，即 raw_contacts 的 id 作为 data 表中的 raw_contact_id
                .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.Contacts.Data.DATA1, contact.email)
                .withValue(ContactsContract.Contacts.Data.DATA2, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build();

        // 声明一个内容操作器列表，将上面的操作器添加到列表中
        ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(operation);
        list.add(name);
        list.add(phone);
        list.add(email);

        // 批量提交四个操作
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, list);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    // 往手机通讯录添加一个联系人信息（包括姓名、电话号码、电子邮箱）
    private void addContacts(ContentResolver resolver, Contact contact) {
        ContentValues values = new ContentValues();
        // 往 raw_contacts 添加联系人记录，并获取添加后的联系人编号
        Uri uri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(uri);

        ContentValues name = new ContentValues();
        // 关联联系人编号
        name.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        // “姓名”的数据类型
        name.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人的姓名
        name.put(ContactsContract.Contacts.Data.DATA2, contact.name);
        // 往提供器添加联系人的姓名记录
        resolver.insert(ContactsContract.Data.CONTENT_URI, name);


        ContentValues phone = new ContentValues();
        // 关联联系人编号
        phone.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        // “电话号码”的数据类型
        phone.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        phone.put(ContactsContract.Contacts.Data.DATA1, contact.phone);
        // 联系类型。1表示家庭，2表示工作
        phone.put(ContactsContract.Contacts.Data.DATA2, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // 往提供器添加联系人的姓名记录
        resolver.insert(ContactsContract.Data.CONTENT_URI, phone);


        ContentValues email = new ContentValues();
        // 关联联系人编号
        email.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
        //  “电子邮箱”的数据类型
        email.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        // 联系人的电子邮箱
        email.put(ContactsContract.Contacts.Data.DATA1, contact.email);
        // 联系类型。1表示家庭，2表示工作
        email.put(ContactsContract.Contacts.Data.DATA2, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        // 往提供器添加联系人的姓名记录
        resolver.insert(ContactsContract.Data.CONTENT_URI, email);
    }
}