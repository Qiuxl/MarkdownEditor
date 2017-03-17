package myMarkdown;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Html2Doc {

	public Html2Doc() {
		// TODO Auto-generated constructor stub
	}
	public static boolean HtmlToWord(String html, String wordFile) {    

        ActiveXComponent app = new ActiveXComponent("Word.Application"); // Æô¶¯word
        try {
            app.setProperty("Visible", new Variant(false));
            Dispatch wordDoc = app.getProperty("Documents").toDispatch();
            wordDoc = Dispatch.invoke(wordDoc, "Add", Dispatch.Method, new Object[0], new int[1]).toDispatch();
            Dispatch.invoke(app.getProperty("Selection").toDispatch(), "InsertFile", Dispatch.Method, new Object[] { html, "", new Variant(false), new Variant(false), new Variant(false) }, new int[3]);
            Dispatch.call(wordDoc, "SaveAs", wordFile); 
            
            Dispatch.call(wordDoc, "Close", new Variant(false));
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            app.invoke("Quit", new Variant[] {});
        }
		return false;
	}
}
