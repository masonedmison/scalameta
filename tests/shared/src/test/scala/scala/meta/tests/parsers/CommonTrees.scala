package scala.meta.tests
package parsers

import scala.meta._

import scala.language.implicitConversions

object CommonTrees {
  trait LowPriorityDefinitions {

    final def tname(name: String): Term.Name = Term.Name(name)
    implicit def implicitStringToTerm(obj: String): Term.Name = tname(obj)

    final def pname(name: String): Type.Name = Type.Name(name)
    implicit def implicitStringToType(obj: String): Type.Name = pname(obj)

  }
}

trait CommonTrees extends CommonTrees.LowPriorityDefinitions {
  object Nothing {
    def unapply(tree: Tree): Boolean = tree match {
      case Type.Name("Nothing") => true
      case _ => false
    }
  }

  object Any {
    def unapply(tree: Tree): Boolean = tree match {
      case Type.Name("Any") => true
      case _ => false
    }
  }

  object EmptySelf {
    def apply(): Self = Self(anon, None)
    def unapply(tree: Tree): Boolean = tree match {
      case Self(Name.Anonymous(), None) => true
      case _ => false
    }
  }

  object EmptyCtor {
    def apply() = Ctor.Primary(Nil, anon, Seq.empty[Term.ParamClause])
    def unapply(tree: Tree): Boolean = tree match {
      case Ctor.Primary(Nil, Name.Anonymous(), Nil) => true
      case _ => false
    }
  }

  object EmptyTemplate {
    def apply(): Template = tpl()
    def unapply(tree: Tree): Boolean = tree match {
      case Template(Nil, Nil, EmptySelf(), Nil) => true
      case _ => false
    }
  }

  final val anon = meta.Name.Anonymous()
  final val phName = meta.Name.Placeholder()
  final val ctor = EmptyCtor()
  final def ctorp(lp: Term.Param*): Ctor.Primary = ctorp(lp.toList)
  final def ctorp(lp: List[Term.Param], lps: List[Term.Param]*): Ctor.Primary = Ctor
    .Primary(Nil, anon, lp :: lps.toList)
  final def ctorp(mod: Mod.ParamsType, lp: Term.Param*): Ctor.Primary = Ctor
    .Primary(Nil, anon, Term.ParamClause(lp.toList, Option(mod)) :: Nil)
  final val slf = meta.Self(anon, None)
  final def self(name: String, tpe: Option[Type] = None): meta.Self = meta.Self(mname(name), tpe)
  final def self(name: String, tpe: Type): meta.Self = self(name, Option(tpe))

  implicit def implicitStringsToTerms(obj: List[String]): List[Term.Name] = obj.map(tname)
  implicit def implicitStringToTermOpt(obj: Option[String]): Option[Term.Name] = obj.map(tname)

  final def mname(name: String): meta.Name = name match {
    case "" => anon
    case "_" => phName
    case _ => tname(name)
  }
  implicit def implicitStringToName(obj: String): meta.Name = mname(obj)
  implicit def implicitStringsToNames(obj: List[String]): List[meta.Name] = obj.map(mname)
  implicit def implicitStringToNameOpt(obj: Option[String]): Option[meta.Name] = obj.map(mname)

  final def tplNoBody(inits: List[Init]): Template = Template(Nil, inits, tplBody())
  final def tplNoBody(inits: Init*): Template = tplNoBody(inits.toList)

  final def tpl(inits: List[Init], body: Template.Body): Template = Template(Nil, inits, body)
  final def tpl(inits: List[Init], stats: List[Stat]): Template = tpl(inits, tplBody(stats: _*))
  final def tpl(stats: List[Stat]): Template = tpl(Nil, stats)
  final def tpl(stats: Stat*): Template = tpl(stats.toList)

  final def tplBody(selfOpt: Option[meta.Self], stats: Stat*): Template.Body = Template
    .Body(selfOpt, stats.toList)
  final def tplBody(self: meta.Self, stats: Stat*): Template.Body = tplBody(Option(self), stats: _*)
  final def tplBody(name: String, stats: Stat*): Template.Body = tplBody(Some(self(name)), stats: _*)
  final def tplBody(stats: Stat*): Template.Body = tplBody(None, stats: _*)

  final def tparam(mods: List[Mod], name: String, tpe: Option[Type] = None): Term.Param = Term
    .Param(mods, mname(name), tpe, None)
  final def tparam(name: String, tpe: Option[Type]): Term.Param = tparam(Nil, name, tpe)
  final def tparam(mods: List[Mod], name: String, tpe: Type): Term.Param =
    tparam(mods, name, Option(tpe))
  final def tparam(name: String, tpe: Type): Term.Param = tparam(Nil, name, tpe)
  final def tparam(name: String): Term.Param = tparam(Nil, name)

  final def tparamval(name: String, tpe: Type) = tparam(List(Mod.ValParam()), name, tpe)
  final def tparamInline(name: String, tpe: Type) = tparam(List(Mod.Inline()), name, tpe)
  final def tparamUsing(name: String, tpe: Type) = tparam(List(Mod.Using()), name, tpe)

  final def tinfix(lt: Term, op: Term.Name, ta: List[Type], rt: Term*): Term.ApplyInfix = Term
    .ApplyInfix(lt, op, ta, rt.toList)
  final def tinfix(lt: Term, op: Term.Name, rt: Term*): Term.ApplyInfix = tinfix(lt, op, Nil, rt: _*)
  final def tpostfix(lt: Term, op: Term.Name): Term.SelectPostfix = Term.SelectPostfix(lt, op)
  final def tselect(lt: Term, op: Term.Name): Term.Select = Term.Select(lt, op)
  final def tapply(fun: Term, args: Term*): Term.Apply = Term.Apply(fun, args.toList)
  final def tapplytype(fun: Term, args: Type*) = Term.ApplyType(fun, args.toList)
  final def tmatch(lt: Term, cases: Case*): Term.Match = Term.Match(lt, cases.toList)
  final def tselectmatch(lt: Term, cases: Case*): Term.SelectMatch = Term
    .SelectMatch(lt, cases.toList)
  final def tfunc(body: Term, params: Term.Param*): Term.Function = Term
    .Function(params.toList, body)
  final def tctxfunc(body: Term, params: Term.Param*): Term.ContextFunction = Term
    .ContextFunction(params.toList, body)
  final def tpolyfunc(body: Term, params: Type.Param*) = Term.PolyFunction(params.toList, body)

  implicit def implicitStringsToTypes(obj: List[String]): List[Type.Name] = obj.map(pname)
  implicit def implicitStringToTypeOpt(obj: Option[String]): Option[Type.Name] = obj.map(pname)

  final def pparam(s: String): Type.Param = pparam(s, noBounds)
  final def pparam(s: String, bounds: Type.Bounds): Type.Param = pparam(Nil, s, bounds)
  final def pparam(
      mods: List[Mod],
      s: String,
      bounds: Type.Bounds = noBounds,
      vb: List[Type] = Nil,
      cb: List[Type] = Nil
  ): Type.Param = {
    val nameTree = s match {
      case "" => anon
      case "_" => phName
      case _ => pname(s)
    }
    Type.Param(mods, nameTree, Nil, bounds, vb, cb)
  }

  final def pinfix(lt: Type, op: Type.Name, rt: Type): Type.ApplyInfix = Type.ApplyInfix(lt, op, rt)
  final def papply(tpe: Type, args: Type*): Type.Apply = Type.Apply(tpe, args.toList)
  final def pfunc(param: List[Type], res: Type): Type.Function = Type.Function(param, res)
  final def pfunc(res: Type, param: Type*): Type.Function = pfunc(param.toList, res)
  final def pctxfunc(param: List[Type], res: Type): Type.ContextFunction = Type
    .ContextFunction(param, res)
  final def pctxfunc(res: Type, param: Type*): Type.ContextFunction = pctxfunc(param.toList, res)
  final def purefunc(param: List[Type], res: Type): Type.PureFunction = Type.PureFunction(param, res)
  final def purefunc(res: Type, param: Type*): Type.PureFunction = purefunc(param.toList, res)
  final def purectxfunc(param: List[Type], res: Type): Type.PureContextFunction = Type
    .PureContextFunction(param, res)
  final def purectxfunc(res: Type, param: Type*): Type.PureContextFunction =
    purectxfunc(param.toList, res)
  final def ppolyfunc(body: Type, params: Type.Param*) = Type.PolyFunction(params.toList, body)

  final def patvar(name: Term.Name): Pat.Var = Pat.Var(name)
  implicit def implicitStringToPatVar(obj: String): Pat.Var = patvar(obj)

  final def patinfix(lt: Pat, op: Term.Name, rt: Pat*): Pat.ExtractInfix = Pat
    .ExtractInfix(lt, op, rt.toList)

  final val noBounds = Type.Bounds(None, None)
  final def loBound(bound: Type): Type.Bounds = Type.Bounds(Some(bound), None)
  final def hiBound(bound: Type): Type.Bounds = Type.Bounds(None, Some(bound))
  final def bounds(lo: Type.Name = null, hi: Type.Name = null): Type.Bounds = Type
    .Bounds(Option(lo), Option(hi))

  final def lit() = Lit.Unit()
  final def bool(v: Boolean) = Lit.Boolean(v)
  final def lit(v: Boolean) = Lit.Boolean(v)
  final def int(v: Int) = Lit.Int(v)
  final def lit(v: Int) = Lit.Int(v)
  final def lit(v: Long) = Lit.Long(v)
  final def dbl(v: String) = Lit.Double(v)
  final def lit(v: Double) = Lit.Double(v)
  final def flt(v: String) = Lit.Float(v)
  final def lit(v: Float) = Lit.Float(v)
  final def str(v: String) = Lit.String(v)
  final def lit(v: String) = str(v)
  final def lit(v: Char) = Lit.Char(v)
  final def sym(v: String) = lit(Symbol(v))
  final def lit(v: Symbol) = Lit.Symbol(v)
  final def init(tpe: Type, args: Term.ArgClause*): Init = init(tpe, args.toList)
  final def init(tpe: Type, args: List[Term.ArgClause] = Nil): Init = Init(tpe, anon, args)
  final def blk(stats: List[Stat]): Term.Block = Term.Block(stats)
  final def blk(stats: Stat*): Term.Block = blk(stats.toList)

  final def stats(vals: Stat*): Stat.Block = Stat.Block(vals.toList)

}
