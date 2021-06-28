package com.example.practicenavermap

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map
import com.example.practicenavermap.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import org.naver.Naver
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    val TAG = "로그"

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var geocoder : Geocoder
    // 네이버 지오코더 때문에 선언
    val naver = Naver(clientId = "9yulgtl3nq", clientSecret = "R9A3oPxTIi1CTqnETRNNGZ5VSFRoSjHPY00biFax")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        geocoder  = Geocoder(this)

        // 현재 위치 불러오기
        val locationmanager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 권한을 확인하는 과정

        // 지도 객체 생성
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.NaverMap_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.NaverMap_fragment, it).commit()
            }
        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this)


    }

    //
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        // 마커 객체 생성 및 지도에 추가
        val marker = Marker()
//        marker.position = LatLng(35.15788038125036, 129.05922326871536)


        // 위치 오버레이 객체 - 자신의 위치를 표현할 때
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        //locationOverlay.position = LatLng(35.15788048125056, 129.05922566871536)

        // naverMap에 locarionSource를 지정하면 위치추적 기능을 사용할 수 있게된다.
        naverMap.locationSource = locationSource

        // 위치 추적 모드에 대해 정할 수 있다
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 위치 변경에 대한 이벤트를 정할 수 있다. 위치 변경 시 감지지
        naverMap.addOnLocationChangeListener { location ->
            mainBinding.textView.text = location.latitude.toString() + location.longitude.toString()
        }

        // 지도가 클릭되면 onMapClick() 콜백 메서드가 호출되며, 파라미터로 클릭된 지점의 화면 좌표와 지도 좌표가 전달됩니다.
        naverMap.setOnMapClickListener { pointF, coord ->
            marker.position = LatLng( coord.latitude , coord.longitude )
            marker.map = naverMap

            Log.d(TAG, "마커 생성 ${coord.latitude} ${coord.longitude} ")

            var location =  geocoder.getFromLocation(coord.latitude,coord.longitude,1)


            // mainBinding.textView.text = coord.latitude.toString() + coord.longitude.toString()
//            val locations = naver.map().reverseGeocode(lat = coord.latitude.toString(), lng = coord.longitude.toString())
            Log.d(TAG, "우당탕탕ㅇ ${location[0]}")
            // locations.items.forEach { location -> location.address }


            //mainBinding.textView.text = locations.toString()
        }




    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}