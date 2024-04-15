// Note that this example might not work locally in Chrome (it works on a web server).
// In other browsers, you may get a prompt that allows you to share location data.
// It may take the browser a while to determine your location (his demonstrates why you should not depend on this information before loading the entire page).

var geo = document.getElementById('geo');
geo.onclick = () => { findGeoLocation() };


if (window.localStorage) {
	var elLat = document.getElementById('lat');
	var elLnt = document.getElementById('lnt');

	elLat.value = localStorage.getItem('lat');
	elLnt.value = localStorage.getItem('lnt');
	
	elLat.onblur = () => {
		localStorage.setItem('lat', elLat.value);
	};
	
	elLnt.onblur = () => {
		localStorage.setItem('lnt', elLnt.value);
	};
}

function findGeoLocation() {	
	var elMap = document.getElementById('loc');
	var elLat = document.getElementById('lat');
	var elLnt = document.getElementById('lnt');
	var msg = 'Sorry, we were unable to get your location.';    
	
	if (Modernizr.geolocation) {                                
	  navigator.geolocation.getCurrentPosition(success, fail);  
	  elMap.textContent = '사용자 위치를 찾고있습니다...';               
	} else {                                                    
	  elMap.textContent = msg;                                  
	}
	
	function success(position) {                                
	  msg = '';              
	  elMap.innerHTML = msg;
	  elLat.value = position.coords.latitude;
	  elLnt.value = position.coords.longitude; 
	  
	  if (window.localStorage) {
		localStorage.setItem('lat', position.coords.latitude);
		localStorage.setItem('lnt', position.coords.longitude);
	  }
	}
	
	function fail(msg) {                                       
	  elMap.textContent = msg;                                 
	  console.log(msg.code);                                   
	}
}