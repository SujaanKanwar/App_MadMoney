package UnitTest;

import android.content.Context;

import com.example.sujan.madmoney.Utility.MoneyStore;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.*;

/**
 * Created by sujan on 14/10/15.
 */

@RunWith(MockitoJUnitRunner.class)
public class MoneyStoreTest {

    @Mock
    private Context context;

//    @Mock
//    private JSONObject jsonObject;

    private String mockEncryptedMoney ="{\"me\":\"You\"}";


    @Test
    public void testStoreBluetoothTransferMoney() throws Exception {

        SecretKeySpec secretKeySpec = MoneyStore.decryptAESKey("fe33DSuiCWJQV95rOk4MclBtFFBcyIhuh+K0B+zzHusC+Ou6L/Ol/V4tmafaHgw/S5fzQF9HGrB+xuQswHk0SZQi0iTypAV8pY2iNVwIYsudmbAxG5n0owiuORiuOC6O5HRZ7bTpsVZo3ly4xMkb0Z7Lma4w4WB1cls+lV+nb0k=");

    }
}