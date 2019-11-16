var COM_TABLE_URL=buildUrlWithContextPath("/commit/comlist");
var ACCEPT_URL=buildUrlWithContextPath("/pr/accept");
var REJECT_URL=buildUrlWithContextPath("/pr/reject");
var PR_CHANGES_URL=buildUrlWithContextPath("/pr/changes");
var BLOB_LIST_URL=buildUrlWithContextPath("/blob/content");
var repo_name;
var pr_name;
var branch_target;
var ID;
var branch_base;
var selectedPath;

function ajaxPRCom(){
    $.ajax({
        url: COM_TABLE_URL,
        type: "get",
        data: {repo:repo_name,
            branch:branch_target },
        dataType: "json",
        success: function(data) {
            /* [ { "SHA-1":"35333ERd33", "Message":"bbb","Commit date": "11/2/29", "Creator":"Or", "List of branches":["master","new","deep"...]},... ] */
            refreshComList(data);
        }
    });

}

function ajaxAccept(){
    $.ajax({
        url: ACCEPT_URL,
        data: {id: ID,},
        success: function(data) {
            alert("The PR accepted");
            window.open("../repository/repository.html?data="+repo_name, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
        }
    });
}

function ajaxReject(message_) {
    $.ajax({
        url: REJECT_URL,
        data: {id: ID,
            message: message_},
        success: function (data) {
            alert("The PR rejected");
            window.open("../repository/repository.html?data="+repo_name, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
        }
    });
}

//["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
function refreshComList(commits) {
    $.each(commits || [], function(index, data) {
        console.log(index + ": " + data);
        var tr="<tr onclick=onClickCom($(this).find(\"td[id='SHA-1']\").text());>";
        var td1="<td id=\"SHA-1\">"+data["SHA-1"]+"</td>";
        var td2="<td>"+data["Message"]+"</td>";
        var td3="<td>"+data["Commit date"]+"</td>";
        var td4="<td>"+data["Creator"]+"</td>";
        var branches= data["List of branches"]
        var td5="<td>"+branches+"</td></tr>";
        $("#ComTable").find('tbody').append(tr+td1+td2+td3+td4+td5);
    });
}

function onClickCom(sha1_) {
    $.ajax({
        url: PR_CHANGES_URL,
        data: {id: ID,
            sha1:sha1_},
        dataType: "json",
        success: function(data) {
            //["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
            document.getElementById("myList").style.display = "block";
            refreshChanges(data);

        }
    });
}

function refreshChanges(files) {
    //[[1:  {"Type (Folder or Blob): Blob","Name: c.txt","Who made the last change: itamar","When was the last update: 13.10.2019-10:18:33:035"}
    // [2: {"Type (Folder or Blob): Blob","Name: c.txt","Who made the last change: itamar","When was the last update: 13.10.2019-10:18:33:035"}*/
    //["Type (Folder or Blob): Blob<br>Name: a.txt<br>Who … the last update: 09.06.2019-20:15:11:232<br><br>"]
    $("#changesList").find('tbody').children().remove();
   // $("#changesList").find('tbody').empty();
    $.each(files || [], function(key,value ) {
        console.log(files[1]);
        console.log(files[2]);
        console.log(files[3]);
    });
    if (files[1].length!==0){
    var parts = (files[1].toString()).split("<br>");
    var type=parts[1].split(':');
    var path=parts[0].split(';');
    var status="created";
    var tr="<tr onclick=myFunction($(this).find(\"td[id='path']\").text(),$(this).find(\"td[id='type']\").text());>";
    var td1="<td id=\"status\">created</td>";
    var td2="<td id=\"type\">"+type[1]+"</td>";
    var td3="<td id=\"path\">"+path[1]+"</td></tr>";
    $("#changesList").find('tbody').append(tr+td1+td2+td3);
    console.log(this);
    }
    if (files[2].length!==0) {
        var parts = (files[2].toString()).split('<br>');
        var type = parts[1].split(':');
        var path = parts[0].split(';');
        var status = "updated";
        var tr = "<tr onclick=myFunction($(this).find(\"td[id='path']\").text(),$(this).find(\"td[id='type']\").text());>";
        var td1="<td id=\"status\">updated</td>";
        var td2="<td id=\"type\">"+type[1]+"</td>";
        var td3="<td id=\"path\">"+path[1]+"</td></tr>";
        $("#changesList").find('tbody').append(tr + td1 + td2+td3);
        console.log(this);
    }
    if (files[3].length!==0) {
        var parts = (files[3].toString()).split('<br>');
        var type = parts[1].split(':');
        var path = parts[0].split(';');
        var status = "deleted";
        var tr = "<tr onclick=myFunction($(this).find(\"td[id='path']\").text(),$(this).find(\"td[id='type']\").text());>";
        var td1="<td id=\"status\">deleted</td>";
        var td2="<td id=\"type\">"+type[1]+"</td>";
        var td3="<td id=\"path\">"+path[1]+"</td></tr>";
        $("#changesList").find('tbody').append(tr + td1 + td2+td3);
        console.log(this);
    }
}

function myFunction(path,type) {
    console.log("path: " + path);
    console.log("type: " + type);
    selectedPath=path;
    if (type == " Blob") {
        ajaxBlob();
        document.getElementById("myForm1").style.display = "block";
    }

}

function ajaxBlob(){
    $.ajax({
        url: BLOB_LIST_URL,
        data: {path: selectedPath},
        success: function(data) {
            beforeModifiedFileContent=data;
            refreshBlob(beforeModifiedFileContent);
        },
        error: function (thrownError) {
            alert(thrownError);
        }
    });
}

function refreshBlob(beforeModifiedFileContent) {
    console.log(beforeModifiedFileContent);
    //document.getElementById("blob").innerHTML = beforeModifiedFileContent;
    $("#blob").val(beforeModifiedFileContent);
}


$(function() {
    var parts = location.search.substring(1).split('&');
    var arr= parts[0].split('=');
    var arr1= parts[1].split('=');
    var arr2= parts[2].split('=');
    var arr3=parts[3].split('=');
    repo_name =decodeURI(arr[1]);
    pr_name=decodeURI(arr1[1]);
    branch_target=decodeURI(arr2[1]);
    branch_base=decodeURI(arr3[1]);
    ID=decodeURI(location.search.split('ID=')[1]);
    console.log(repo_name);
    console.log(pr_name);
    console.log(branch_target);
    console.log(branch_base);
    console.log(ID);
    ajaxPRCom();
});


$(document).ready(function() {
    $( "#accept" ).click(function() {
        ajaxAccept();
    });
});

$(document).ready(function() {
    $( "#reject" ).click(function() {
        var message = prompt("Enter Message:");
        if (message == null || message == "") {
            alert("User cancelled the prompt.");
            return;}
        else { ajaxReject(message);}
    });
});

function closeForm() {
    document.getElementById("myList").style.display = "none";
}

function closeForm1() {
    document.getElementById("myForm1").style.display = "none";
}

