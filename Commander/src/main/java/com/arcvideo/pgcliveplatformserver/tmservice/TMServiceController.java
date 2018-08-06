package com.arcvideo.pgcliveplatformserver.tmservice;

import com.arcvideo.pgcliveplatformserver.entity.ServerSetting;
import com.arcvideo.pgcliveplatformserver.model.ResultBean;
import com.arcvideo.pgcliveplatformserver.model.ServerType;
import com.arcvideo.pgcliveplatformserver.service.server.ServerSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by slw on 2018/3/28.
 */
@Controller
public class TMServiceController {
    static private Logger logger = LoggerFactory.getLogger(TMServiceController.class);

    private static String SourceParamName	= "url";

    private static final int Action_Auto	= 0; // no action specified
    private static final int Action_List	= 1; // action="list"
    private static final int Action_Read	= 2; // action="read"
    private static final int Action_Write	= 4; // action="write"

    private static final int Source_None	= 0; // local file/folder
    private static final int Source_File	= 1; // local file/folder
    private static final int Source_Http	= 2; // http resource, not supported for now!
    private static final int Source_Udp	 	= 3; // udp resource
    private static final int Source_Ftp		= 4; // ftp resource

    private static final int Block_Size	= 0xF8E0; // 0x10000; // 0x2000; // 0x2000000 for native transferTo(java.nio.channels.FileChannel.transferTo)

    private static DatagramDistributor g_udpDistributor = new DatagramDistributor();

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private ServerSettingService serverSettingService;

    @RequestMapping(value = "/tms.content")
    protected void content(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value = "url", required = true) String url,
                          @RequestParam(value = "action", required = false) String strAction) {
        int action = Action_Auto;
        if (strAction != null && (strAction.equalsIgnoreCase("write") || strAction.equalsIgnoreCase("w"))) {
            action = Action_Write;
        }
        if (action==Action_Auto) {
            doGet(request, response, url, strAction);
        } else {
            doPut(request, response, url, strAction);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response, String url, String strAction) {
        int type = Source_None;

        if (url != null) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
                if (url.toLowerCase().startsWith("http")) {
                    type = Source_Http;
                } else if (url.toLowerCase().startsWith("udp")) {
                    type = Source_Udp;
                } else if (url.toLowerCase().startsWith("ftp")) {
                    type = Source_Ftp;
                } else {
                    type = Source_File;
                }
            } catch (Exception e) {
                logger.error("tmservice URLDecoder error", e);
                type = Source_None;
            }
        } else {
            type = Source_None;
        }

        if (type == Source_File) {
            try {
                writeFileContent(request, response, new File(url));
            } catch (IOException e) {
                logger.error("tmservice writeFileContent error", e);
            }
        } else {
            try {
                writeFileContent(request, response, null);
            } catch (IOException e) {
                logger.error("tmservice writeFileContent error", e);
            }
        }
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response, String strUrl, String strAction) {
        int action = Action_Auto;
        int type = Source_None;
        File file = null;
        URL url = null;
        try {
            if (strUrl != null) {
                strUrl = URLDecoder.decode(strUrl, "utf-8");

                if (strUrl.toLowerCase().startsWith("http")) {
                    type = Source_Http;
                } else if (strUrl.toLowerCase().startsWith("udp")) {
                    type = Source_Udp;
                } else if (strUrl.toLowerCase().startsWith("ftp")) {
                    type = Source_Ftp;
                } else {
                    type = Source_File;
                }

                if (type == Source_File) {
                    file = new File(strUrl);
                    if (file == null || file.exists() == false) {
                        type = Source_None;
                    }
                } else if (type == Source_Http || type == Source_Ftp) {
                    url = new URL(strUrl);
                }
            }
        } catch (Exception e) {
            logger.error("tmservice doGet error", e);
            file = null;
            url = null;
            type = Source_None;
        }

        if (strAction != null) {
            if (strAction.equalsIgnoreCase("list")) {
                action = Action_List;
            } else if (strAction.equalsIgnoreCase("read")) {
                action = Action_Read;
            }
        }

        if (action==Action_Auto) {
            if (type==Source_Http || type==Source_Udp || type==Source_Ftp) {
                action = Action_Read;
            } else if (file==null || file.exists()==false) {
                action = Action_List;
            } else if (file.isDirectory()) {
                action = Action_List;
            } else {
                action = Action_Read;
            }
        }

        if (type == Source_None) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            switch (action) {
                case Action_Read:
                    switch(type) {
                        case Source_Http:
                            try {
                                readHttpContent(request, response, url);
                            } catch (IOException e) {
                                logger.error("tmservice readHttpContent error", e);
                            }
                            break;

                        case Source_Ftp:
                            try {
                                readFtpContent(request, response, url);
                            } catch (IOException e) {
                                logger.error("tmservice readFtpContent error", e);
                            }
                            break;

                        case Source_Udp:
                            try {
                                //new API
                                readUdpData(request, response, strUrl);
                            } catch (Exception e) {
                                logger.error("tmservice readUdpData error", e);
                            }
                            break;

                        case Source_File:
                        default:
                            try {
                                readFileContent(request, response, file);
                            } catch (IOException e) {
                                logger.error("tmservice readFileContent", e);
                            }
                            break;
                    }
                    break;

                case Action_List:
                default:
                    try {
                        listFolderContent(request, response, file);
                    } catch (IOException e) {
                        logger.error("tmservice listFolderContent", e);
                    }
                    break;
            }
        }
    }

    protected long readHttpContent(HttpServletRequest request, HttpServletResponse response, URL url) throws IOException {
        // not supported for now!
        return -1;
    }

    /**
     * Read the ftp content
     * @param response servlet response
     * @param url resource
     */
    protected long readFtpContent(HttpServletRequest request, HttpServletResponse response, URL url) throws IOException {
        FTPClient ftp = new FTPClient(url);

        String filePath = url.getPath();
        if (filePath.charAt(0)=='/') {
            filePath = filePath.substring(1);
        }

        // Get the file size
        long length = ftp.size(filePath);

        int restSupported = 1;
        long start = 0, end = length;
        long pos2[] = new long[2];
        if (ParseHttpRangeHeader(request.getHeader("Range"), pos2) != 0) {
            if (pos2[0] > 0) {
                start = pos2[0];
            }
            if (pos2[1] > 0) {
                end = pos2[1];
            }
            if (start > 0 && ftp.restart(start) < 0) {
                // it is not supported to restart at some position!
                start = 0;
                restSupported = 0;
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } else {
                if (end > start) {
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                } else {
                    response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                }
                String bytesRange = String.format("bytes %d-%d/%d", start, end-1, length);
                response.addHeader("Content-Range", bytesRange);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        response.addHeader("Accept-Ranges", (restSupported==0)? "none" : "bytes");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Content-Length", Long.toString(end-start));
        // Don't set MIME type according to file extension, because file extension may be incorrect!
		/*
		try
		{
			String mimeType = request.getServletContext().getMimeType(filePath);
			if (mimeType != null)
			{
				response.setContentType(mimeType);
			}
		}
		catch (Exception e)
		{
		}
		*/

        long lenSent = 0;
        try {
            // Transfer the file to the output.
            OutputStream output = response.getOutputStream();
            lenSent = ftp.transfer(filePath, end-start, output);
            output.close();
        } catch (Exception e) {
            // other exception
            if (lenSent >= 0) {
                lenSent = -1;
            }
        } finally {
            // Quit from the FTP server.
            ftp.disconnect();
        }

        return lenSent;
    }

    private void readUdpData(HttpServletRequest request, HttpServletResponse response, String path) {
        ServletOutputStream servletOutputStream = null;
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader("Accept-Ranges", "none");
            //to skip crossing domain error.
            response.addHeader("Access-Control-Allow-Origin", "*");
            if (NativeFunction.ChunkedTransfer == 0) {
                response.addHeader("Content-Length", Long.toString(NativeFunction.PresumedTotalBytes));
            }
            servletOutputStream = response.getOutputStream();

            g_udpDistributor.processPathOutput(path, servletOutputStream);
        } catch(SocketException e) {
            logger.error("tmservice readUdpData error", e);
        } catch(Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.error("tmservice readUdpData error", e);
        } finally {
            if(servletOutputStream != null) {
                try {
                    servletOutputStream.close();
                } catch (IOException e) {
                    logger.error("tmservice readUdpData error", e);
                }
            }
        }
    }

    /**
     * Read the file content and output to servlet response
     * @param request servlet request
     * @param response servlet response
     */
    protected long readFileContent(HttpServletRequest request, HttpServletResponse response, File file) throws IOException {
        long length = (file==null) ? -1 : file.length();
        if (length <= 0) {
            return length;
        }

        long start = 0, end = length;
        long pos2[] = new long[2];
        if (ParseHttpRangeHeader(request.getHeader("Range"), pos2) != 0) {
            if (pos2[0] > 0) {
                start = pos2[0];
            }
            if (pos2[1] > 0) {
                end = pos2[1];
            }
            if (end > start) {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            }
            String bytesRange = String.format("bytes %d-%d/%d", start, end-1, length);
            response.addHeader("Content-Range", bytesRange);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        response.addHeader("Accept-Ranges", "bytes");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Content-Length", Long.toString(end-start));
        // Don't set MIME type according to file extension, because file extension may be incorrect!
		/*
		try
		{
			String mimeType = request.getServletContext().getMimeType(file.getAbsolutePath());
			if (mimeType != null)
			{
				response.setContentType(mimeType);
			}
		}
		catch (Exception e)
		{
		}
		*/

        long pos = start;
        if (start < end) {
            FileInputStream input = null;
            ServletOutputStream output = null;
            try {
                input = new FileInputStream(file);
                if (pos != 0) {
                    pos = input.skip(pos);
                }
                output = response.getOutputStream();
                byte[] buffer = new byte[Block_Size];
                int bytesRead = 0;
                while (pos < end && (bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    pos += bytesRead;
                }
                output.flush();
                buffer = null;
            } catch (SocketTimeoutException e) {
                // timeout to read/write data
                //e.printStackTrace();
            } catch (IOException e) {
                // input or output stream is closed
                //e.printStackTrace();
            } catch (Exception e) {
                // other exception
                //e.printStackTrace();
            } finally {
                if (input != null) {
                    input.close();
                    input = null;
                }
                if (output != null) {
                    output.close();
                    output = null;
                }
            }
    		/*
	        FileChannel fileChannel = null;
			WritableByteChannel outChannel = null;
			try
			{
		        fileChannel = new FileInputStream(file).getChannel();
				outChannel = Channels.newChannel(response.getOutputStream());
				while (pos < end)
				{
					pos += fileChannel.transferTo(pos, (((end-pos) > Block_Size) ? Block_Size : (end-pos)), outChannel);
				}
			}
			catch (Exception e) // EOFException, ClosedChannelException and so on
			{
				//e.printStackTrace();
			}
			finally
			{
				if (fileChannel != null)
				{
					fileChannel.close();
					fileChannel = null;
				}
				if (outChannel != null)
				{
					outChannel.close();
					outChannel = null;
				}
			}
			*/
        }

        return (pos-start);
    }

    /**
     * Write the file content from servlet request
     * @param request servlet request
     * @param response servlet response
     */
    protected long writeFileContent(HttpServletRequest request, HttpServletResponse response, File file) throws IOException {
        int error = 0;
        long pos = 0;

        if (file != null) {
            long end;
            String strLength = request.getHeader("Content-Length");
            if (strLength != null && (end = Long.parseLong(strLength)) > pos) {
                FileChannel fileChannel = null;
                ReadableByteChannel inputChannel = null;
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fileChannel = fos.getChannel();
                    inputChannel = Channels.newChannel(request.getInputStream());
                    while (pos < end) {
                        pos += fileChannel.transferFrom(inputChannel, pos, (((end-pos) > Block_Size) ? Block_Size : (end-pos)));
                    }
                } catch (SecurityException e) {
                    error = -1;
                } catch (FileNotFoundException e) {
                    error = -2;
                } catch (Exception e) {
                    if (pos < end) {
                        error = -9;
                    }
                } finally {
                    if (fos != null) {
                        fos.close();
                        fos = null;
                    }
                    if (inputChannel != null) {
                        inputChannel.close();
                        inputChannel = null;
                    }
                }
            } else {
                error = -20;
            }
        } else {
            error = -21;
        }

        switch (error) {
            case 0:
                response.setStatus(HttpServletResponse.SC_OK);
                break;
            case -1:
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                break;
            case -2:
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                break;
            case -9:
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                break;
            case -20:
                response.setStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
                break;
            case -21:
                response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                break;
        }

        PrintWriter writer = null;
        try {
            String strResult = Integer.toString(error);
            if (strResult != null) {
                response.addHeader("Content-Length", Integer.toString(strResult.length()));
                writer = response.getWriter();
                writer.print(strResult);
            }
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }

        return pos;
    }

    /**
     * List the folder contents
     * @param response servlet response
     */
    protected void listFolderContent(HttpServletRequest request, HttpServletResponse response, File file) throws IOException {
        String templetFilePath = null;
        try {
            //templetFilePath = Content.class.getResource("ContentList.xsl").getFile();
        } catch (Exception e) {
            templetFilePath = null;
        }
        response.setContentType("text/xml;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        if (out != null) {
            try {
                UtilHelper.outputXml2Response(genContentsDocument(file, request.getRequestURL().toString()), templetFilePath, out);
            } finally {
                out.close();
            }
        }
    }

    protected String getStringValue(Element elemParent, String tagName) {
        NodeList nodeList;
        Node node0, node;
        String str = null;

        nodeList = elemParent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            node0 = nodeList.item(0);
            if (node0 != null) {
                node = node0.getFirstChild();
                if (node != null) {
                    str = node.getNodeValue();
                }
            }
        }

        return str;
    }

    protected Document genContentsDocument(File file, String reqUrl) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Node node = genContentsNode(doc, file, reqUrl);
            if (node != null) doc.appendChild(node);
            return doc;
        } catch (Exception e) {
            return null;
        }
    }

    protected Node genContentsNode(Document ownerDoc, File file, String reqUrl) {
        try  {
            Element rootElem = null;
            File[] fs = null;

            try {
                if (file != null) {
                    fs = file.listFiles();
                    if (fs != null) {
                        Arrays.sort(fs, new Comparator<File>() {
                            @Override
                            public int compare(File f1, File f2) {
                                if (f1.isFile() && f2.isDirectory())
                                    return -1;
                                else if (f1.isDirectory() && f2.isFile())
                                    return 1;
                                else
                                    return f1.getName().compareToIgnoreCase(f2.getName());
                            }
                        });
                    }
                }
            } catch (Exception e) {
            }

            if (fs != null) {
                Element itemElem, elem;
                String  strName, strType, strUri;
                String preUri = String.format("%s?%s=", reqUrl, SourceParamName);

                rootElem = ownerDoc.createElement("contents");
                rootElem.setAttribute("type", "1");
                for (int i = 0; i < fs.length; ++i) {
                    strType = fs[i].isFile() ? "0" : "1"; // 0 indicates file, 1 indicates folder
                    strName = fs[i].getName();
                    strUri = preUri + fs[i].getAbsolutePath();

                    itemElem = ownerDoc.createElement("item");
                    rootElem.appendChild(itemElem);

                    itemElem.setAttribute("type", strType);

                    elem = ownerDoc.createElement("name");
                    elem.appendChild(ownerDoc.createTextNode(strName));
                    itemElem.appendChild(elem);

                    elem = ownerDoc.createElement("uri");
                    elem.appendChild(ownerDoc.createTextNode(strUri));
                    itemElem.appendChild(elem);
                }
            } else {
                rootElem = ownerDoc.createElement("error");
                rootElem.setAttribute("code", "-1");
                rootElem.appendChild(ownerDoc.createTextNode("file/folder doesn't exist!"));
            }

            return rootElem;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse the http Range header
     * @param strRange: the header string of http range
     * @param pos: a reference of long array to store start, end position.
     * return 0 if no http Range header, else return 1.
     */
    protected int ParseHttpRangeHeader(String strRange, long pos[]) {
        if (strRange != null) {
            String strStart = null, strEnd = null;
            int index = strRange.indexOf("bytes");
            if (index >= 0) {
                index += 6;
                int dashIndex = strRange.indexOf("-");
                if (dashIndex >= index) {
                    strStart = strRange.substring(index, dashIndex);
                    strEnd = strRange.substring(dashIndex+1);
                } else {
                    strStart = strRange.substring(index);
                }
            }
            if (pos != null) {
                pos[0] = (strStart==null || strStart.isEmpty()) ? 0 : Long.parseLong(strStart);
                pos[1] = (strEnd==null || strEnd.isEmpty()) ? -1 : Long.parseLong(strEnd)+1;
            }

            return 1;
        } else {
            return 0;
        }
    }
}
