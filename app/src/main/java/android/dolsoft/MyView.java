package android.dolsoft;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MyView extends View{

	public static final long FPS_MS = 1000/2;
	private static final int MARGIN = 2;
	private static final int MSG_BLINK = 1;

	private final Handler mHandler = new Handler(new MyHandler());
		
	private Paint 	mLinePaint;
    private Paint 	mBmpPaint;
    private Bitmap 	mBmpPlayer1;
	private Bitmap 	mBmpPlayer2;
		
	private Bitmap m_BitmapBg;
	
	
	private int 	m_nCurrentPlayer = 1; // 1 : player1, 2 : player2, 0: normal
	private Rect 	m_rectBitmap = new Rect();
	private Rect 	m_rectCell 	= new Rect();
	private final Rect m_rectBlink = new Rect();
	private boolean mBlinkDisplayOff;
	
	private int 	m_nRow		= 0;
	private int		m_nCol		= 0;
	private int 	m_nCellSize	= 40;
	private Rect 	m_rectTable = new Rect();
	private int[][] m_matrixState;
	
	 
	public MyView(Context context, AttributeSet attrs) 
	{
				
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initTable();
	    		
		m_BitmapBg  = getResBitmap(R.drawable.bg_test);
		    
		mBmpPlayer1 = getResBitmap(R.drawable.lib_cross);
		mBmpPlayer2 = getResBitmap(R.drawable.lib_circle);
		
		if(mBmpPlayer1!=null)
			m_rectBitmap.set(0, 0, mBmpPlayer1.getWidth()-1, mBmpPlayer1.getHeight());
		
		mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		mLinePaint = new Paint();
        mLinePaint.setColor(0xFFAAAAAA);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Style.STROKE);
        
    	m_rectCell.set(MARGIN, MARGIN,m_nCellSize, m_nCellSize);
	}

	private void initTable()
	{		
		 m_nCol = ( getWidth()*2 -2*MARGIN)/m_nCellSize;
		 m_nRow = ( getHeight()*2-2*MARGIN)/m_nCellSize;
							
		
		int  nHeight= (m_nCellSize * m_nRow) + MARGIN; 
		int  nWidth = (m_nCellSize * m_nCol) + MARGIN; 
		
		m_rectTable.set(MARGIN, MARGIN, nWidth, nHeight);
		
		if(m_matrixState!=null)
		{
			m_matrixState.clone();
			m_matrixState = null;			
		}
		m_matrixState = new int[m_nRow][m_nCol];
		resetMatrixState();
	}
	private void resetMatrixState()
	{
		for(int i=0; i< m_nRow; i++)
			for(int j=0; j< m_nCol; j++)
				m_matrixState[i][j]=0;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		try{
					
			int x = m_rectTable.left;
			int y = m_rectTable.top;
			int xr = m_rectTable.right; // x right
			int yb = m_rectTable.bottom; // y bottom
			
			canvas.drawBitmap(m_BitmapBg, new Rect(0, 0, m_BitmapBg.getWidth(), m_BitmapBg.getHeight()), m_rectTable, mBmpPaint);
			canvas.drawRect(m_rectTable, mLinePaint);
			
			for(int i=1; i<m_nRow; i++)
			{			
				canvas.drawLine(x, y+i*m_nCellSize,xr, x+i*m_nCellSize, mLinePaint);	
			}
			
			for(int i=1; i<m_nCol; i++)
			{
				canvas.drawLine(x+i*m_nCellSize, y, x+i*m_nCellSize, yb, mLinePaint);					
			}
			
			int nState=0;
			for(int row=0; row< m_nRow; row++)
				for(int col=0; col< m_nCol; col++)
				{
					nState = m_matrixState[row][col];
					if(nState == 1)
					{
						m_rectCell.offsetTo(col*m_nCellSize+MARGIN, row*m_nCellSize+MARGIN);
						canvas.drawBitmap(mBmpPlayer1,m_rectBitmap, m_rectCell , mBmpPaint);
					}
					else if(nState == 2)
					{
						m_rectCell.offsetTo(col*m_nCellSize+MARGIN, row*m_nCellSize+MARGIN);
						canvas.drawBitmap(mBmpPlayer2,m_rectBitmap, m_rectCell , mBmpPaint);
					}
				}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Exception = " + e.toString());
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		initTable();
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		
		if(action==MotionEvent.ACTION_DOWN)
		{
			return true;
		}
		else if(action == MotionEvent.ACTION_UP)
		{
			
			try{
				int nCurX = (int)event.getX();
				int nCurY = (int)event.getY();
			
				if(nCurX <= m_rectTable.right && nCurX>= m_rectTable.left && nCurY <= m_rectTable.bottom && nCurY>= m_rectTable.top )
				{
					int  nColIndex =  (nCurX-MARGIN)/m_nCellSize;
				
					int nRowIndex =  (nCurY-MARGIN)/m_nCellSize;
				
					// kiem tra o trong hay khong
					if(m_matrixState[nRowIndex][nColIndex]!=0)
					{
						return true;
					}
					
					
					if(m_nCurrentPlayer==1)
					{
						m_matrixState[nRowIndex][nColIndex] =1;
						m_nCurrentPlayer =2;
					}
					//else if(m_nCurrentPlayer==2)
					{
						Point p = ComputerPlay(m_matrixState, m_nRow, m_nCol);
						
						m_matrixState[p.x][p.y] =2;
						m_nCurrentPlayer =1;
					}
							
					if(TestGameFinish(m_matrixState, m_nRow, m_nCol)==true)
					{
						resetMatrixState();
					}
					//m_rectBlink.set(nRowIndex*m_nCellSize+MARGIN, nColIndex*m_nCellSize+MARGIN, nRowIndex*m_nCellSize+MARGIN + m_nCellSize, nColIndex*m_nCellSize+MARGIN + m_nCellSize );
					
					//mBlinkDisplayOff = false;
					// mHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
					 
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROW = " + nRowIndex + " Y = " + nColIndex );
					invalidate();
				}
				return true;
			
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Exception = " + e.toString());
			}
		}
		
		return false;
	}

	// can tim hieu ky hon
	private Bitmap getResBitmap(int bmpResId) 
    {
        Options opts = new Options();
        opts.inDither = false;

        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, bmpResId, opts);

        if (bmp == null && isInEditMode()) {
            // BitmapFactory.decodeResource doesn't work from the rendering
            // library in Eclipse's Graphical Layout Editor. Use this workaround instead.

            Drawable d = res.getDrawable(bmpResId);
            int w = d.getIntrinsicWidth();
            int h = d.getIntrinsicHeight();
            bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            d.setBounds(0, 0, w - 1, h - 1);
            d.draw(c);
        }

        return bmp;
    }
	
	///////////////////////////////////////////////////////////////////
	
	public int getState(Point p)
     {
         return m_matrixState[p.x][p.y];
     }
	
	 public boolean IsInside(int i, int j, int row, int column)
     {
         if (i >= 0 && i < row && j >= 0 && j < column)
             return true;
         return false;
     }
	 

     
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     
     private class MyHandler implements Callback 
     {
         public boolean handleMessage(Message msg) 
         {
             if (msg.what == MSG_BLINK) 
             {
                 if (m_rectBlink.top != 0) 
                 {
                     mBlinkDisplayOff = !mBlinkDisplayOff;
                     invalidate(m_rectBlink);

                     if (!mHandler.hasMessages(MSG_BLINK)) 
                     {
                         mHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
                     }
                 }
                 return true;
             }
             return false;
         }
     }
     
     
     
     
 	// kiem tra duong chay cua mot hang
     private Point runline(int[][] chessBoard, int row, int column, int i, int j, int dx, int dy)
     {
         int x = i, y = j;
         do
         {
             x += dx;
             y += dy;
         } while (this.IsInside(x, y, row, column) && chessBoard[x][y] == chessBoard[i][j]);
         return (new Point(x, y));
     }

     public Point ComputerPlay(int[][] chessBoard, int row, int column)
     {
         Point ret;
         ret = calculate(chessBoard, row, column);// Tinh toan, chon diem di
     
         return ret;
     }

     private Point calculate(int[][] chessBoard, int row, int column)
     {
         int tmp = 0;
         Point t = new Point();

         Random rd = new Random();

         for (int i = 0; i < row; i++)
         {
             for (int j = 0; j < column; j++)
                 if (chessBoard[i][j] == 0) // chua co quan co
                 {
                     int ptx = evaluate(chessBoard, i, j, row, column);
                     if (ptx > tmp)
                     {
                         tmp = ptx;
                         t.x = i;
                         t.y = j;
                         if (tmp >= 10000)
                             return t;
                     }
                     else
                         if (ptx == tmp && rd.nextInt(2) == 0)
                         {
                             t.x = i;
                             t.y = j;
                         }
                 }
         }
         return t;
     }
     
  // Ham uoc luong gia tri tai diem (i,j)
     private int evaluate(int[][] chessBoard, int i, int j, int row, int column)
     {
         int num, totalPoint = 0;
         int[] dx = { 1, 1, 1, 0 };
         int[] dy = { -1, 0, 1, 1 };
         // Gia su quan co duoc dat o day 
         chessBoard[i][j] = 2;

         for (int h = 0; h < 4; h++)
         {
             Point p, q;
             p = runline(chessBoard, row, column, i, j, dx[h], dy[h]);
             q = runline(chessBoard, row, column, i, j, (int)-dx[h], (int)-dy[h]);

             int len; // so quan co thang hang
             if (p.x != q.x)
                 len = (int)Math.abs(p.x - q.x);
             else
                 len = (int)Math.abs(p.y - q.y);

             if (len < 6)
             {
                 // 1 trong 2 diem nam ngoai hay da bi danh dau
                 if (!(this.IsInside((int)p.x, (int)p.y, row, column) && this.getState( p) == 0 &&
                     this.IsInside((int)q.x, (int)q.y, row, column) && this.getState( q) == 0))
                     len--;
                 // ca hai diem nam ngoai hay ca hai diem da danh dau 
                 if (!((this.IsInside((int)p.x, (int)p.y, row, column) && this.getState( p) == 0) ||
                     (this.IsInside((int)q.x, (int)q.y, row, column) && this.getState( q) == 0)))
                     len = 2;
             }

             switch (len)
             {
                 case 1: totalPoint -= 1; break;
                 case 2: break;
                 case 3: totalPoint += 2; break;
                 case 4: totalPoint += 8; break;
                 case 5: totalPoint += 5000; break;
                 default: totalPoint += 10000; break;
             }

             if (totalPoint >= 10000)
             {
                 chessBoard[i][j] = 0; // khoi phuc 
                 return totalPoint;
             }
         }

         // Kiem tra hai duong 
         num = 0;
         for (int h = 0; h < 4; h++)
         {
             Point p, p1;
             p = runline(chessBoard, row, column, i, j, dx[h], dy[h]);
             p1 = p;
             // Kiem tra duong thu 2
             if (this.IsInside((int)p1.x, (int)p1.y, row, column) && this.getState( p1) == 0)
                 p1 = runline(chessBoard, row, column, (int)p1.x, (int)p1.y, dx[h], dy[h]);

             Point q, q1;
             q = runline(chessBoard, row, column, i, j, (int)-dx[h], (int)-dy[h]);

             // xet diem cuoi voi diem con x1,y1 cua diem dau x,y
             if (this.IsInside((int)p1.x, (int)p1.y, row, column) && this.getState( p1) == 0 &&
                 this.IsInside((int)q.x, (int)q.y, row, column) && this.getState( q) == 0)
             {
                 if (Math.abs(p1.x - q.x) >= 6 || Math.abs(p1.y - q.y) >= 6)
                 {
                     num++;
                     if (num == 2)
                     {
                         totalPoint += 150;
                         break; // for h
                     }
                 }
             }

             q1 = q;
             if (this.IsInside((int)q1.x, (int)q1.y, row, column) && this.getState( q1) == 0)
                 q1 = runline(chessBoard, row, column, (int)q1.x, (int)q1.y, dx[h], dy[h]);

             // xet diem dau voi diem con u1,v1 cua diem cuoi u,v
             if (this.IsInside((int)q1.x, (int)q1.y, row, column) && this.getState( q1) == 0 &&
                 this.IsInside((int)p.x, (int)p.y, row, column) && this.getState( p) == 0)
             {
                 if (Math.abs(q1.x - p.x) >= 6 || Math.abs(q1.y - p.y) >= 6)
                 {
                     num++;
                     if (num == 2)
                     {
                         totalPoint += 150;
                         break;
                     }
                 }
             }
         }

         //
         // uoc luong duong di neu User dat o vi tri i,j.
         //

         chessBoard[i][j] = 1;
         for (int h = 0; h < 4; h++)
         {
             Point p, q;
             p = runline(chessBoard, row, column, i, j, dx[h], dy[h]);
             q = runline(chessBoard, row, column, i, j, (int)-dx[h], (int)-dy[h]);

             int len; // so quan co thang hang
             if (p.x != q.x)
                 len = (int)Math.abs(p.x - q.x);
             else
                 len = (int)Math.abs(p.y - q.y);
             if (len < 6)
             {
                 // 1 trong 2 diem nam ngoai hay da bi danh dau
                 if (!(this.IsInside((int)p.x, (int)p.y, row, column) && this.getState( p) == 0 &&
                     this.IsInside((int)q.x, (int)q.y, row, column) && this.getState( q) == 0))
                     len--;
                 // diem p,q nam ngoai ngay user da danh dau
                 if (!this.IsInside((int)p.x, (int)p.y, row, column) || this.getState( p) == 2 ||
                     !this.IsInside((int)q.x, (int)q.y, row, column) || this.getState( q) == 2)
                     continue; // next h
             }
             switch (len)
             {
                 case 1: totalPoint -= 1; break;
                 case 2: break;
                 case 3: totalPoint += 2; break;
                 case 4: totalPoint += 6; break;
                 case 5: totalPoint += 800; break;
                 default: totalPoint += 7000; break;
             }
         }
         // Kiem tra hai duong 
         num = 0;
         for (int h = 0; h < 4; h++)
         {
             Point p, p1;
             p = runline(chessBoard, row, column, i, j, dx[h], dy[h]);
             p1 = p;
             // Kiem tra duong thu 2
             if (this.IsInside((int)p1.x, (int)p1.y, row, column) && this.getState( p1) == 0)
                 p1 = runline(chessBoard, row, column, (int)p1.x, (int)p1.y, dx[h], dy[h]);

             Point q, q1;
             q = runline(chessBoard, row, column, i, j, (int)-dx[h], (int)-dy[h]);

             // xet diem cuoi voi diem con x1,y1 cua diem dau x,y
             if (this.IsInside((int)p1.x, (int)p1.y, row, column) && this.getState( p1) == 0 &&
                 this.IsInside((int)q.x, (int)q.y, row, column) && this.getState( q) == 0)
             {
                 if (Math.abs(p1.x - q.x) >= 6 || Math.abs(p1.y - q.y) >= 6)
                 {
                     num++;
                     if (num == 2)
                     {
                         totalPoint += 30;
                         break; // for h
                     }
                 }
             }

             q1 = q;
             if (this.IsInside((int)q1.x, (int)q1.y, row, column) && this.getState( q1) == 0)
                 q1 = runline(chessBoard, row, column, (int)q1.x, (int)q1.y, dx[h], dy[h]);

             // xet diem dau voi diem con u1,v1 cua diem cuoi u,v
             if (this.IsInside((int)q1.x, (int)q1.y, row, column) && this.getState( q1) == 0 &&
                 this.IsInside((int)p.x, (int)p.y, row, column) && this.getState( p) == 0)
             {
                 if (Math.abs(q1.x - p.x) >= 6 || Math.abs(q1.y - p.y) >= 6)
                 {
                     num++;
                     if (num == 2)
                     {
                         totalPoint += 30;
                         break;
                     }
                 }
             }

         }
         chessBoard[i][j] = 0; // khoi phuc 
         return totalPoint;
     }
     
     
     public boolean TestGameFinish(int[][] chessBoard, int row, int column)//,  Point winPoint, Point stepPoint)
     {
         int i, j, k;
         int[] dx =  { 1, 1, 1, 0 };
         int[] dy =  { -1, 0, 1, 1 };
         
         for (i = 0; i < row; i++)
             for (j = 0; j < column; j++)
             {
                 if (chessBoard[i][j] != 0)
                 {
                     for (k = 0; k < 4; k++)
                     {
                         int count = 0, x = i, y = j;
                         while (count < 5 && this.IsInside(x, y, row, column) && chessBoard[x][y] == chessBoard[i][j])
                         {
                             count++;
                             x += dx[k];
                             y += dy[k];
                         }
                         
                         // save winner infomation
                         if (count == 5)
                         {
                        	 //winPoint = new Point(i,j);
                        	// stepPoint = new Point(dx[k], dy[k]);
                             return true;
                         }
                     }
                 }
             }
         return false;
     }

}

