function update_doc(doc_link) {
	//document.getElementById("doc_frame").src=doc_link; 
	document.getElementById("doc_content").innerHTML='<object type="text/html" data="' + doc_link +  '" ></object>';
}