function sendDeleteRequest() {
	var id = this.getAttribute('id');
	
	var xhr = new XMLHttpRequest();
	var url = 'search-history';
	var param = 'id=' + id;
	xhr.open('POST', url, false);
	xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	
	xhr.onload = function() {
		if (xhr.status === 200) {
			location.reload();
		}
	}
	
	xhr.send(param);
}

var deleteItems = document.getElementsByClassName('delete');
for (var i = 0; i < deleteItems.length; i++) {
	deleteItems[i].addEventListener('click', sendDeleteRequest, false);
}