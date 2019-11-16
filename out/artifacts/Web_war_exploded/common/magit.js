var USER_LIST_URL = buildUrlWithContextPath("userslist");
var PR_LIST_URL = buildUrlWithContextPath("PRlist");

// ["moshe","nachum","nachche"...]
function refreshUsersList(users) {
    $("#userslist").empty();
    $.each(users || [], function(index, username) {
        console.log("Adding user #" + index + ": " + username);
        $('<li><a href="#">' + username + '</a></li>').appendTo($("#userslist"));
    });
}

// ["username: moshe/branch: head/PRbranch: new/10:00 06/03/18/open ",..]
function refreshPRList(PRs) {
    $("#PRlist").empty();
    $.each(PRs || [], function(index, PRdetails) {
        console.log("Adding user #" + index + ": " + PRdetails);
        $('<li><a href="#">' + PRdetails + '</a></li>').appendTo($("#PRlist"));
    });
}


function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");

    // add the relevant entries
    $.each(entries || [], appendChatEntry);

    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

function ajaxUsersList() {
    $.ajax({
        url: PR_LIST_URL,
        success: function(PRs) {
            refreshUsersList(PRs);
        }
    });
}

function ajaxPRList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshPRList(users);
        }
    });
}


function ajaxChatContent() {
    $.ajax({
        url: CHAT_LIST_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
             {
                "entries": [
                    {
                        "chatString":"Hi",
                        "username":"bbb",
                        "time":1485548397514
                    },
                    {
                        "chatString":"Hello",
                        "username":"bbb",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             */
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}


    $(function() {
        $("#uploadForm").submit(function() {
            var formData = new FormData();
            formData.append("fake-key-1", file1);

            $.ajax({
                method:'POST',
                data: formData,
                url: this.action,
                processData: false,
                contentType: false,
                timeout: 4000,
                error: function(e) {
                    console.error("Failed to submit");
                    $("#result").text("Failed to get result from server " + e);
                },
                success: function(r) {
                    $("#result").text(r);
                }
            });
            return false;
        })
    })





function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}


$(function() {
    setInterval(ajaxUsersList, refreshRate);
    triggerAjaxChatContent();
});

$(function() {
    setInterval(ajaxPRList, refreshRate);
    triggerAjaxChatContent();
});