package evilWithin.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.HeartAnimListener;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.shop.Merchant;
import com.megacrit.cardcrawl.vfx.SpeechBubble;
import slimebound.SlimeboundMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class HeartMerchant implements Disposable {
    private static final CharacterStrings characterStrings;
    public static final String[] NAMES;
    public static final String[] TEXT;
    public static final String[] ENDING_TEXT;
    public AnimatedNpc anim;
    public static final float DRAW_X;
    public static final float DRAW_Y;
    public Hitbox hb;
    private ArrayList<AbstractCard> cards1;
    private ArrayList<AbstractCard> cards2;
    private ArrayList<String> idleMessages;
    private float speechTimer;
    private float introAnimTimer;
    private boolean saidWelcome;
    private static final float HB_WIDTH = 360.0F;
    private static final float HB_HEIGHT = 60.0F;
    private static final float SPEECH_DURATION = 3.0F;
    private int shopScreen;
    protected float modX;
    protected float modY;

    public HeartMerchant() {
        this(0.0F, 0.0F, 1);
    }

    public HeartMerchant(float x, float y, int newShopScreen) {
       // SlimeboundMod.logger.info("New Heart Merchant made");
        this.cards1 = new ArrayList();
        this.cards2 = new ArrayList();
        this.idleMessages = new ArrayList();
        this.speechTimer = 1.5F;
        this.saidWelcome = false;
        this.shopScreen = 1;
        this.anim = new AnimatedNpc(1334.0F * Settings.scale, AbstractDungeon.floorY - 60.0F * Settings.scale, "images/npcs/heart/skeleton.atlas", "images/npcs/heart/skeleton.json", "idle");

        anim.addListener(new HeartAnimListener());

        AbstractCard c;
        for(c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true).makeCopy(); c.color == AbstractCard.CardColor.COLORLESS; c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true).makeCopy()) {
        }

        this.cards1.add(c);

        for(c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true).makeCopy(); Objects.equals(c.cardID, ((AbstractCard)this.cards1.get(this.cards1.size() - 1)).cardID) || c.color == AbstractCard.CardColor.COLORLESS; c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.ATTACK, true).makeCopy()) {
        }

        this.cards1.add(c);

        for(c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true).makeCopy(); c.color == AbstractCard.CardColor.COLORLESS; c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true).makeCopy()) {
        }

        this.cards1.add(c);

        for(c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true).makeCopy(); Objects.equals(c.cardID, ((AbstractCard)this.cards1.get(this.cards1.size() - 1)).cardID) || c.color == AbstractCard.CardColor.COLORLESS; c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.SKILL, true).makeCopy()) {
        }

        this.cards1.add(c);

        for(c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.POWER, true).makeCopy(); c.color == AbstractCard.CardColor.COLORLESS; c = AbstractDungeon.getCardFromPool(AbstractDungeon.rollRarity(), AbstractCard.CardType.POWER, true).makeCopy()) {
        }

        this.cards1.add(c);
        this.cards2.add(AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.UNCOMMON).makeCopy());
        this.cards2.add(AbstractDungeon.getColorlessCardFromPool(AbstractCard.CardRarity.RARE).makeCopy());
        if (AbstractDungeon.id.equals("TheEnding")) {
            Collections.addAll(this.idleMessages, ENDING_TEXT);
        } else {
            Collections.addAll(this.idleMessages, TEXT);
        }

        this.speechTimer = 1.5F;
        this.modX = x;
        this.modY = y;
        this.shopScreen = newShopScreen;
        AbstractDungeon.shopScreen.init(this.cards1, this.cards2);
    }

    public void update() {

        if (introAnimTimer > 0F) {
            this.introAnimTimer -= Gdx.graphics.getDeltaTime();
            float animY = Interpolation.pow2.apply(AbstractDungeon.floorY - 60.0F * Settings.scale,AbstractDungeon.floorY + 700F * Settings.scale,this.introAnimTimer / 2F);
            this.anim.skeleton.setY(animY);
        }

        this.hb.update();

        if ((this.hb.hovered && InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp && !AbstractDungeon.isFadingOut && !AbstractDungeon.player.viewingRelics) {
            AbstractDungeon.overlayMenu.proceedButton.setLabel(NAMES[0]);
            this.saidWelcome = true;
            AbstractDungeon.shopScreen.open();
            this.hb.hovered = false;
        }

        this.speechTimer -= Gdx.graphics.getDeltaTime();
        if (this.speechTimer < 0.0F && this.shopScreen == 1) {
            String msg = (String)this.idleMessages.get(MathUtils.random(0, this.idleMessages.size() - 1));
            if (!this.saidWelcome) {
                this.saidWelcome = true;
                this.welcomeSfx();
                msg = NAMES[1];
            } else {
                this.playMiscSfx();
            }

            if (MathUtils.randomBoolean()) {
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX - 50.0F * Settings.scale, this.hb.cY + 70.0F * Settings.scale, 3.0F, msg, false));
            } else {
                AbstractDungeon.effectList.add(new SpeechBubble(this.hb.cX + 50.0F * Settings.scale, this.hb.cY + 70.0F * Settings.scale, 3.0F, msg, true));
            }

            this.speechTimer = MathUtils.random(40.0F, 60.0F);
        }



    }

    private void welcomeSfx() {
        CardCrawlGame.sound.play("HEART_SIMPLE");
    }

    private void playMiscSfx() {
        CardCrawlGame.sound.play("HEART_SIMPLE");
    }

    public void render(SpriteBatch sb) {

        if (Settings.isControllerMode) {
            sb.setColor(Color.WHITE);
            sb.draw(CInputActionSet.select.getKeyImg(), DRAW_X - 32.0F + 150.0F * Settings.scale, DRAW_Y - 32.0F + 100.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }

        if (this.anim != null) {
            this.anim.render(sb);
        }

        if (this.hb != null) this.hb.render(sb);
        //SlimeboundMod.logger.info("Heart Merchant render tick.");
    }

    public void dispose() {
        if (this.anim != null) {
            this.anim.dispose();
           // SlimeboundMod.logger.info("Heart Merchant disposed.");
        }

    }

    public void spawnHitbox(){
        this.hb = new Hitbox(500.0F * Settings.scale, 700.0F * Settings.scale);
        this.hb.move(DRAW_X * Settings.scale, DRAW_Y * Settings.scale);
        this.introAnimTimer = 2F;
    }

    static {
        characterStrings = CardCrawlGame.languagePack.getCharacterString("HeartMerchant");
        NAMES = characterStrings.NAMES;
        TEXT = characterStrings.TEXT;
        ENDING_TEXT = characterStrings.OPTIONS;
        DRAW_X = (float)Settings.WIDTH * 0.7F * Settings.scale;
        DRAW_Y = AbstractDungeon.floorY + 300.0F * Settings.scale;
    }
}