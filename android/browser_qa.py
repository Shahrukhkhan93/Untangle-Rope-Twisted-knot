from pathlib import Path
from playwright.sync_api import sync_playwright
import json
root=Path(__file__).resolve().parent;p=root/'app/src/main/assets/index.html'; out={}
with sync_playwright() as pw:
 b=pw.chromium.launch(executable_path='/usr/bin/chromium',headless=True,args=['--no-sandbox','--disable-dev-shm-usage'])
 pg=b.new_page(viewport={'width':390,'height':780});errors=[];pg.on('pageerror',lambda e:errors.append(str(e)))
 setup = """<script>window.requests=[];window.events=[];window.store=JSON.parse(STORE);Object.defineProperty(window,'localStorage',{configurable:true,value:{getItem:k=>store[k]??null,setItem:(k,v)=>store[k]=String(v)}});window.RopeNative={showAd:(kind,id)=>requests.push({kind,id}),event:(...args)=>events.push(args),privacy:()=>{},setBannerVisible:()=>{}};</script>"""
 def boot(saved={}):
  pg.set_content(p.read_text().replace('<head>','<head>'+setup.replace('STORE',json.dumps(json.dumps(saved))),1),wait_until='domcontentloaded');ready()
 def ready():
  pg.wait_for_function('!!window.__ropeTest');pg.evaluate("()=>{document.querySelector('#oceanStartup')?.remove();document.querySelector('.game').inert=false}");pg.wait_for_timeout(50)
 boot()
 badge=pg.locator('#hint .rope-ad-badge'); assert not badge.is_visible();pg.locator('#hint').click();assert badge.is_visible(); assert pg.evaluate('requests.length')==0;out['first_hint_free_badge_appears']=True
 pg.locator('#hint').click();assert pg.evaluate('requests.at(-1).kind')=='rewarded';pg.evaluate('ropeAdResult(requests.at(-1).id,false)');assert 'not completed' in pg.locator('#status').inner_text();out['skipped_reward_no_hint']=True
 pg.locator('#hint').click();pg.evaluate('ropeAdResult(requests.at(-1).id,true)');assert 'Suggested move' in pg.locator('#status').inner_text();assert pg.evaluate('events.some(x=>x[0]==="hint_used"&&x[2]===1)');out['completed_reward_reveals_hint']=True
 pg.locator('#reset').click();assert badge.is_visible();out['restart_keeps_used_free_hint']=True
 saved=pg.evaluate('store');pg.close();pg=b.new_page(viewport={'width':390,'height':780});pg.on('pageerror',lambda e:errors.append(str(e)));boot(saved);badge=pg.locator('#hint .rope-ad-badge');assert badge.is_visible();out['reload_keeps_used_free_hint']=True
 pg.evaluate('__ropeTest.chooseLevel(2)');assert not badge.is_visible();out['new_level_free_hint']=True
 def solve(n):
  pg.evaluate(f'__ropeTest.chooseLevel({n})');pg.wait_for_timeout(100);sol=pg.evaluate(f'__ropeTest.definitions()[{n-1}].solution');r=pg.locator('#board').bounding_box();cw=pg.locator('#board').evaluate('(e)=>e.width/2');ch=pg.locator('#board').evaluate('(e)=>e.height/2')
  def pt(h):
   x=35+70*(h%6);y=78+73*(h//6);return r['x']+(35+(x-35)*(cw-70)/350)/cw*r['width'],r['y']+y/ch*r['height']
  for a,z in sol:
   x,y=pt(a);xx,yy=pt(z);pg.mouse.move(x,y);pg.mouse.down();pg.mouse.move(xx,yy,steps=35);pg.mouse.up();pg.wait_for_timeout(500)
  pg.wait_for_function('__ropeTest.state().win',timeout=8000)
 solve(1);before=pg.evaluate('requests.length');pg.locator('#winNext').click();assert pg.evaluate('requests.length')==before;out['no_interstitial_after_one_completion']=True
 solve(2);pg.locator('#winNext').click();assert pg.evaluate('requests.at(-1).kind')=='interstitial';pg.evaluate('ropeAdResult(requests.at(-1).id,false)');assert pg.evaluate('__ropeTest.state().level')==3;out['interstitial_after_two_unavailable_still_advances']=True
 solve(1);before=pg.evaluate('requests.length');pg.locator('#winNext').click();assert pg.evaluate('requests.length')==before;out['replay_not_counted']=True
 pg.evaluate('__ropeTest.chooseLevel(9)');pg.locator('#hint').click();pg.wait_for_timeout(120);pg.screenshot(path=str(root/'hint-badge-preview.png'));out['js_errors']=errors
 b.close()
(root/'browser-qa.json').write_text(json.dumps(out,indent=2));print(json.dumps(out,indent=2))
