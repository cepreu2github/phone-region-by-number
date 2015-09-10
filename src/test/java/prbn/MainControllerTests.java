package prbn;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PhoneRegionByNumberApplication.class)
@WebAppConfiguration
public class MainControllerTests {
    @InjectMocks
    private MainController controller;
    @Mock
    private IDBService dbService;
    @Mock
    private INetworkService networkService;

    private static final String dateTime = "2015-09-10 11:19:38.0";
    private static final String number = "9145678999";
    private static final String region = "REG";

    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void reinitMocks(){
        reset(dbService, networkService);
    }

    @Test
    public void getUpdateDatetimeTest() {
        initDbService(null, dateTime);
        assertEquals(controller.getUpdateDatetime(), "\"" + dateTime + "\"");
    }

    @Test
    public void performUpdateTest() throws IOException {
        // hash is null
        initNetworkService("hash", "content");
        initDbService(null, "NEVER");
        controller.performUpdate();
        verify(dbService, atLeastOnce()).fillTableFromCSV(anyString());
        verify(dbService, atLeastOnce()).updateHash(anyString(), eq("hash"));
        // hashes are same
        initNetworkService("hash", "content");
        initDbService("hash", dateTime);
        controller.performUpdate();
        verify(dbService, never()).fillTableFromCSV(anyString());
        verify(dbService, never()).updateTableFromCSV(anyString(), anyInt());
        // hashes are different
        initNetworkService("hash2", "content2");
        initDbService("hash", dateTime);
        controller.performUpdate();
        verify(dbService, atLeastOnce()).updateTableFromCSV(anyString(), anyInt());
    }

    @Test(expected=MainController.BadPhoneNumberException.class)
    public void getRegionForNumberTest() throws MainController.BadPhoneNumberException {
        initDbService(null, null);
        assertEquals("\"" + region + "\"", controller.getRegionForNumber(number));
        assertEquals("\"" + region + "\"", controller.getRegionForNumber("8 (914) 56 - 78 - 999"));
        controller.getRegionForNumber("0921903209092310");
        assertTrue(false);
    }

    private void initDbService(String hash, String date){
        reset(dbService);
        when(dbService.getHash(anyString())).thenReturn(hash);
        when(dbService.updateTableFromCSV(anyString(), anyInt())).thenReturn(new int[]{1, 2, 3});
        when(dbService.getUpdateDatetime()).thenReturn(date);
        when(dbService.checkNumber(number)).thenReturn(region);
    }

    private  void initNetworkService(String hash, String content) throws IOException {
        reset(networkService);
        when(networkService.getETagFrom((URL) any())).thenReturn(hash);
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        when(networkService.getStreamFrom((URL) any())).thenReturn(stream);
    }

}
