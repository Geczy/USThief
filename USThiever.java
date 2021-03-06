import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.api.events.MessageEvent;
import org.rev317.api.events.listeners.MessageListener;
import org.rev317.api.methods.Interfaces;
import org.rev317.api.methods.Inventory;
import org.rev317.api.methods.Npcs;
import org.rev317.api.methods.Players;
import org.rev317.api.methods.SceneObjects;
import org.rev317.api.methods.Skill;
import org.rev317.api.wrappers.hud.Item;
import org.rev317.api.wrappers.interactive.Npc;
import org.rev317.api.wrappers.scene.Area;
import org.rev317.api.wrappers.scene.SceneObject;
import org.rev317.api.wrappers.scene.Tile;

@ScriptManifest( author = "Brookpc", category = Category.THIEVING, description = "Steals and Sells items on UltimateScape 2", name = "USThiever", servers = { "UltimateScape" }, version = 1.5 )
public class USThiever extends Script implements Paintable, MessageListener
{

	private final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
	public static Area TA = new Area( new Tile( 2676, 3324, 0 ), new Tile( 2648, 3324, 0 ), new Tile( 2645, 3289, 0 ), new Tile( 2672, 3289, 0 ) );
	public int npcID;
	public int stallID;
	public int startlvl;
	public int[] sellIDs = { 950, 1891, 1901, 2309, 958, 4658, 2007 }; // Silk, Cake, Chocolate
																		// Slice,Bread,Wolf
																		// Fur,Silver Pot,Spice
	public int curlvl;
	public int lvlcount;
	public int cashMade;
	public int infID;
	private final Color color1 = new Color( 229, 255, 59 );
	private final Font font2 = new Font( "Arial", 0, 14 );
	private final Timer RUNTIME = new Timer();
	public static Image img1;


	@Override
	public boolean onExecute()
	{
		img1 = getImage( "http://i.imgur.com/5b9BJFi.png" );
		startlvl = Skill.THIEVING.getLevel();
		curlvl = Skill.THIEVING.getLevel();
		if( curlvl < 20 ) {
			stallID = 1616;
		} // Bread
		if( curlvl >= 20 && curlvl < 35 ) {
			stallID = 1615;
		} // Silk
		if( curlvl >= 35 && curlvl < 50 ) {
			stallID = 1619;
		} // Fur
		if( curlvl >= 50 && curlvl < 65 ) {
			stallID = 1614;
		} // Silver
		if( curlvl >= 65 && curlvl < 75 ) {
			stallID = 1618;
		} // Spice
		if( curlvl >= 75 ) {
			stallID = 1617;
		} // Gems

		strategies.add( new steal() );
		strategies.add( new trade() );
		provide( strategies );
		return true;
	}


	public static Image getImage( String url )
	{
		try {
			return ImageIO.read( new URL( url ) );
		} catch( IOException e ) {
			return null;
		}
	}


	public void atlvlchange()
	{
		curlvl = Skill.THIEVING.getLevel();
		lvlcount = ( curlvl - startlvl );
		return;
	}


	@Override
	public void onFinish()
	{

	}

	public class steal implements Strategy
	{

		@Override
		public boolean activate()
		{
			return ! Inventory.isFull()
					&& TA.contains( Players.getLocal().getLocation() );
		}


		@Override
		public void execute()
		{
			atlvlchange();
			for( SceneObject i: SceneObjects.getNearest( stallID ) ) {
				;
				if( i.isOnScreen() ) {
					i.interact( "Steal-from" );
					Time.sleep( 150 );
				} else {
					i.getLocation().clickMM();
					Time.sleep( 100 );
				}
			}
			curlvl = Skill.THIEVING.getLevel();
			if( curlvl < 20 ) {
				stallID = 1616;
			} // Bread
			if( curlvl >= 20 && curlvl < 35 ) {
				stallID = 1615;
			} // Silk
			if( curlvl >= 35 && curlvl < 50 ) {
				stallID = 1619;
			} // Fur
			if( curlvl >= 50 && curlvl < 65 ) {
				stallID = 1614;
			} // Silver
			if( curlvl >= 65 && curlvl < 75 ) {
				stallID = 1618;
			} // Spice
			if( curlvl >= 75 ) {
				stallID = 1617;
			} // Gems
		}
	}

	public class trade implements Strategy
	{

		@Override
		public boolean activate()
		{
			final Npc Sells[] = Npcs.getNearest( 2270 );
			final Npc Marty = Sells[0];
			return Inventory.isFull()
					&& TA.contains( Players.getLocal().getLocation() )
					&& Marty != null;
		}


		@Override
		public void execute()
		{
			final Npc Sells[] = Npcs.getNearest( 2270 );
			final Npc Marty = Sells[0];

			if( ! Marty.isOnScreen() ) {
				Tile NLoc = Marty.getLocation();
				NLoc.clickMM();
				Time.sleep( 500 );
			}
			if( Interfaces.getOpenInterfaceId() != 3824 && Marty != null ) {
				Npcs.getNearest( 2270 )[0].interact( "Trade" );
				Time.sleep( 200 );
			} else if( Interfaces.getOpenInterfaceId() == 3824 ) {
				for( Item i: Inventory.getItems( sellIDs ) ) {
					i.interact( "Sell 50" );
				}
				Time.sleep( 1000 );
			} else if( Marty == null ) {
				Time.sleep( 200 );
			}
			Time.sleep( 100 );
		}

	}


	@Override
	public void messageReceived( MessageEvent arg0 )
	{

	}


	@Override
	public void paint( Graphics arg0 )
	{
		Graphics2D g = ( Graphics2D )arg0;
		g.setColor( new Color( 0f, 0f, 0f, .5f ) );
		g.fillRect( 4, 23, 130, 75 );
		g.drawImage( img1, 4, 23, null );
		g.setFont( font2 );
		g.setColor( color1 );
		g.drawString( "Levels Gained: " + lvlcount, 6, 58 );
		g.drawString( "Runtime: " + RUNTIME, 6, 71 );
		g.drawString( "Cash Made: N/A", 6, 84 );
		g.drawString( "Current Level: " + curlvl, 6, 97 );

	}

}
