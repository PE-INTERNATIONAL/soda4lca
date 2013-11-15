/* --- Font resizer with a session cookie --- */

function getCookieVal(offset)	{
	var endstr=document.cookie.indexOf (";", offset);
	if (endstr==-1) endstr=document.cookie.length;
	return unescape(document.cookie.substring(offset, endstr));
	}

function readCookie(cookieName)	{
	var arg=cookieName+"=";
	var alen=arg.length;
	var clen=document.cookie.length;
	var i=0;
	while (i<clen)	{
		var j=i+alen;
		if (document.cookie.substring(i, j)==arg) return getCookieVal(j);
		i=document.cookie.indexOf(" ",i)+1;
		if (i==0) break;
		}
	return null;
	}

function writeCookie(cookieName, fontSizeValue)	{
	var argv=writeCookie.arguments;
	var argc=writeCookie.arguments.length;
	var expires=(argc > 2) ? argv[2] : null;
	var path=(argc > 3) ? argv[3] : null;
	var domain=(argc > 4) ? argv[4] : null;
	var secure=(argc > 5) ? argv[5] : false;
	document.cookie=cookieName+"="+escape(fontSizeValue)+
	((expires==null) ? "" : ("; expires="+expires.toGMTString()))+
	((path==null) ? "" : ("; path="+path))+
	((domain==null) ? "" : ("; domain="+domain))+
	((secure==true) ? "; secure" : "");
	}

//Specify affected tags. Add or remove from list:       
// var tgs = new Array( 'div','td','tr', 'li');
var tgs = new Array('body');

//Specify spectrum of different font sizes:
var szs = new Array('x-small','small', 'medium', 'large','x-large' );
var cookievalue = readCookie('fontsize');
var defaultStartSz = 2;
var startSz = (cookievalue) ? Math.abs(cookievalue) : defaultStartSz;
var sz = 0;

function ts(inc) {

	trgt = 'html';

	if (!document.getElementById) return
	var d = document,cEl = null,sz = startSz,i,j,cTags;
	
	sz += inc;

	writeCookie('fontsize', sz);
	
	if ( sz < 2 ) sz = 2;
	if ( sz > 4 ) sz = 4;
	startSz = sz;
		
	if ( !( cEl = d.getElementById( trgt ) ) ) cEl = d.getElementsByTagName( trgt )[ 0 ];

	cEl.style.fontSize = szs[ sz ];

	for ( i = 0 ; i < tgs.length ; i++ ) {
		cTags = cEl.getElementsByTagName( tgs[ i ] );
		for ( j = 0 ; j < cTags.length ; j++ ) cTags[ j ].style.fontSize = szs[ sz ];
	}
}

function initFontSize() {
	if(navigator.userAgent.indexOf('MSIE 6') != -1)
	{
		document.write("<style>#decreaseText {display: inline;}</style>");
		document.write("<style>#increaseText {display: inline;}</style>");
	}
	else
	{
		document.write("<style>#fontSize a[href='javascript:ts(-1)'] {display: inline;}</style>");
		document.write("<style>#fontSize a[href='javascript:ts(1)'] {display: inline;}</style>");
	}
	document.getElementById('fontSize').style.display = 'block';
	document.getElementById('fontSize').style.width = '60px';
	if(startSz!=defaultStartSz)ts(0);
}
